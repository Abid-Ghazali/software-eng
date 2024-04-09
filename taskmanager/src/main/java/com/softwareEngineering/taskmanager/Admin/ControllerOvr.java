package com.softwareEngineering.taskmanager.Admin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.*;
import java.util.*;

public class ControllerOvr {
    @FXML
    public Label acc_nr;
    public Label tsk_nr;
    public Label done_nr;
    @FXML
    private TableView<Map<String, Object>> tsk_tableView;
    String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
    String username = "taskmanager_manager";
    String password = "cC314bj4d";

    public void initialize() {
        String countAcc = "SELECT COUNT(*) FROM \"Accounts\"";
        String countTsk = "SELECT COUNT(*) FROM \"Tasks\"";
        String countDone = "SELECT COUNT(*) FROM \"Tasks\" WHERE task_status = 'Done'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement accStatement = connection.prepareStatement(countAcc);
             PreparedStatement tskStatement = connection.prepareStatement(countTsk);
             PreparedStatement doneStatement = connection.prepareStatement(countDone);
             ResultSet accResultSet = accStatement.executeQuery();
             ResultSet tskResultSet = tskStatement.executeQuery();
             ResultSet doneResultSet = doneStatement.executeQuery()) {

            if (accResultSet.next()) {
                int accountCount = accResultSet.getInt(1);
                acc_nr.setText(String.valueOf(accountCount));
            }

            if (tskResultSet.next()) {
                int taskCount = tskResultSet.getInt(1);
                tsk_nr.setText(String.valueOf(taskCount));
            }

            if (doneResultSet.next()) {
                int doneCount = doneResultSet.getInt(1);
                done_nr.setText(String.valueOf(doneCount));
            }

            // Populate the TableView with data
            initializeTableView();

        } catch (SQLException e) {
            e.printStackTrace();
            msgbox("Failed to initialize. Please check your internet connection and try again.");
        }
    }

    private void initializeTableView() throws SQLException {
        // Retrieve data from the database
        List<Map<String, Object>> taskList = getAllTasks();

        // Create TableColumn for each column in your data
        for (String columnName : taskList.get(0).keySet()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new MapValueFactory<>(columnName));
            tsk_tableView.getColumns().add(column);
        }

        // Convert the data to a format suitable for the TableView
        ObservableList<Map<String, Object>> observableTaskList = FXCollections.observableArrayList(taskList);

        // Set the data in the TableView
        tsk_tableView.setItems(observableTaskList);
    }

    private List<Map<String, Object>> getAllTasks() {
        String SQL = "SELECT * FROM \"Tasks\"";
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>(); // Use LinkedHashMap to maintain column order
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return resultList;
    }

    public static class MapValueFactory<T> implements Callback<TableColumn.CellDataFeatures<Map<String, Object>, T>, ObservableValue<T>> {
        private final String key;

        public MapValueFactory(String key) {
            this.key = key;
        }

        @Override
        public ObservableValue<T> call(TableColumn.CellDataFeatures<Map<String, Object>, T> param) {
            Object value = param.getValue().get(key);
            return new SimpleObjectProperty<>((T) value);
        }
    }

    public void msgbox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info..");
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Get the dialog pane and message label
        DialogPane dialogPane = alert.getDialogPane();
        Label messageLabel = (Label) dialogPane.lookup(".content.label");
        // Apply CSS styles to the message label
        dialogPane.setStyle("-fx-background-color: #1d1d1d;");
        messageLabel.setStyle(" -fx-font-size: 11pt;\n" +
                "    -fx-font-family: \"Segoe UI Semibold\";\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-opacity: 1;");

        alert.showAndWait();
    }
}