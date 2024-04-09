package com.softwareEngineering.taskmanager.Client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.util.*;

public class TaskTableView {
    public TaskTableView(int userID) {
        this.userID = userID;
    }
    private TableView<Map<String, Object>> tableView;
    private int userID;

    private enum TaskStatus {
        NOT_STARTED("Not Started"),
        IN_PROGRESS("In Progress"),
        DONE("Done");
        private final String str;
        TaskStatus(String str) {
            this.str = str;
        }
        @Override
        public String toString() {
            return this.str;
        }
    }

    public void showTaskTableView() {

       // BorderPane borderPane = new BorderPane();
        // Initialize your TableView
        tableView = new TableView<>();
        Stage s = new Stage();

        // Set up the scene
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 400);
        scene.getStylesheets().add(Objects.requireNonNull(TaskTableView.class.getResource("tableview.css")).toExternalForm());

        // Set the stage
        s.setTitle("Workerbee's Tasks");
        s.setScene(scene);

        // Populate the TableView with data
        initializeTableView(this.userID);

        // Add the TableView to the center of the BorderPane
        borderPane.setCenter(tableView);

        // Add the logout button to the top of the BorderPane
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            // Eventhandler
            LogIn login = new LogIn();
            login.start(new Stage());
            s.close(); // Close the current stage
        });
        borderPane.setTop(logoutButton);
        // Show the stage
        s.show();
    }

    private void initializeTableView(int userID) {
        List<Map<String, Object>> taskList = getMyTasks(userID);

        // Sort the taskList based on task_id
        taskList.sort(Comparator.comparingLong(task -> (Long) task.get("task_id")));

        for (String columnName : taskList.get(0).keySet()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);

            if (columnName.equalsIgnoreCase("task_status")) {
                TableColumn<Map<String, Object>, Object> statusCol = new TableColumn<>("task_status");
                statusCol.setCellValueFactory(new MapValueFactory<>("task_status"));

                // Set up the ComboBoxTableCell for the task_status column
                statusCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(TaskStatus.values())));

                // Make the task_status column editable
                statusCol.setOnEditCommit(event -> {
                    Map<String, Object> rowData = event.getRowValue();
                    rowData.put("task_status", event.getNewValue());
                    updateTaskStatus((Long) rowData.get("task_id"), event.getNewValue().toString());
                });

                tableView.getColumns().add(statusCol);
            } else {
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                tableView.getColumns().add(column);
            }
        }

        tableView.setEditable(true);

        // Convert the data to a format suitable for the TableView
        ObservableList<Map<String, Object>> observableTaskList = FXCollections.observableArrayList(taskList);

        // Set the data in the TableView
        tableView.setItems(observableTaskList);

        setRowColors();
    }

    // Add a method to set the row color based on task status
    private void setRowColors() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle(""); // Default style
                } else {
                    // Assuming the status is stored in the "task_status" column
                    Object status = item.get("task_status");

                    // Set different colors based on the task status
                    if (status != null) {
                        switch (status.toString()) {
                            case "Not Started" -> setStyle("-fx-background-color: lightcoral;");
                            case "In Progress" -> setStyle("-fx-background-color: lightyellow;");
                            case "Done" -> setStyle("-fx-background-color: lightgreen;");
                            default -> setStyle(""); // Default style
                        }
                    }
                }
            }
        });
    }

    // Modify the updateTaskStatus method to call setRowColors after updating the status
    private void updateTaskStatus(long taskId, String newStatus) {
        // Implement the database update logic here
        String SQL = String.format("UPDATE \"Tasks\" SET task_status = '%s' WHERE task_id = %d;", newStatus, taskId);

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(SQL);
            System.out.println("Task status updated!");

            // Call setRowColors after updating the status
            setRowColors();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String user = "taskmanager_workerbee";
        String password = "s2lIoSvMZ";
        return DriverManager.getConnection(url, user, password);
    }

    private List<Map<String, Object>> getMyTasks(int userID) {
        String SQL = String.format("SELECT * FROM \"Tasks\" WHERE \"Tasks\".assignee_id = %d;", userID);
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
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

    // Custom Callback to extract values from the map
    public class MapValueFactory<T> implements Callback<TableColumn.CellDataFeatures<Map<String, Object>, T>, ObservableValue<T>> {
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
}

