package com.softwareEngineering.taskmanager.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.*;

public class ControllerAcc{
    String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
    String username = "taskmanager_manager";
    String password = "cC314bj4d";
    public TextField acct_id;
    public TextField fir_na;
    public TextField last_na;
    public TextField pass;
    public TableView<Map<String, Object>> accounts_tableview;
    public void initialize() {
        initializeTableView();
    }
    private void initializeTableView() {
        // Retrieve data from the database
        List<Map<String, Object>> accountsList = getAccounts();

        // Create TableColumn for each column in your data
        for (String columnName : accountsList.get(0).keySet()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new ControllerOvr.MapValueFactory<>(columnName));
            accounts_tableview.getColumns().add(column);
        }

        // Convert the data to a format suitable for the TableView
        ObservableList<Map<String, Object>> observableTaskList = FXCollections.observableArrayList(accountsList);

        // Set the data in the TableView
        accounts_tableview.setItems(observableTaskList);
    }
    private List<Map<String, Object>> getAccounts() {
        String SQL = "SELECT * FROM \"Accounts\"";
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

    public void erstellen(ActionEvent actionEvent) {
        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String username = "taskmanager_manager";
        String password = "cC314bj4d";

        String insertQuery = "INSERT INTO \"Accounts\" (account_id, firstname, lastname, password) VALUES (?, ?, ?, ?)";

        try {
            // Check for empty fields
            if (acct_id.getText().isEmpty() || fir_na.getText().isEmpty() || last_na.getText().isEmpty() || pass.getText().isEmpty()) {
                msgbox("All Fields should be filled.");
            }
            else{

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertQuery);

            statement.setLong(1, Long.parseLong(acct_id.getText()));
            statement.setString(2, fir_na.getText());
            statement.setString(3, last_na.getText());
            statement.setString(4, pass.getText());

            statement.executeUpdate();
            accounts_tableview.getItems().clear();
            this.initializeTableView();
            msgbox("Account Created successfully!");
        }
        } catch (NumberFormatException e) {
            msgbox("Please enter a valid number for Account ID.");
        } catch (IllegalArgumentException e) {
            msgbox(e.getMessage());
        } catch (SQLException e) {
            msgbox("Failed to connect to the database. Please check your internet connection and try again.");
            e.printStackTrace();
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
