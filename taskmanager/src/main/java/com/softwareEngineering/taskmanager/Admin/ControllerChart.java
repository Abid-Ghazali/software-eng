package com.softwareEngineering.taskmanager.Admin;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.sql.*;

public class ControllerChart {
    @FXML
    public BarChart<String, Number> barChart;
    @FXML
    PieChart pieChart;
    int done;
    int inProgress;
    int notStarted;

    public void erstellen() {

        barChart.getData().clear();
        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String username = "taskmanager_manager";
        String password = "cC314bj4d";

        //  A query that joins the two tables and return worker last name and how many tasks they have done
        String query = "SELECT a.lastname, COUNT(*) as taskCount " +
                "FROM \"Tasks\" t " +
                "JOIN \"Accounts\" a ON t.assignee_id = a.account_id " +
                "WHERE t.task_status = 'Done' " +
                "GROUP BY a.lastname " +
                "ORDER BY taskCount DESC " +
                "LIMIT 2";

        // Connect to the database and catching any possible error that can happened while connecting to the database
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            PreparedStatement multi = connection.prepareStatement(query);

            // Retrieve the data from the database
            ResultSet resultSet = multi.executeQuery();

            // prepare the barchart to plot the best 2 Workerbee
            //barChart.setTitle("Top 2 Workerbees with Most Completed Tasks");
            while (resultSet.next()) {
                int taskCount = resultSet.getInt(2);
                String person = resultSet.getString(1);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(person);
                series.getData().add(new XYChart.Data<>(person,taskCount ));
                barChart.getData().add(series);
            }

            // Close the database connection
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            msgbox("Something went wrong! check your connection");
            e.printStackTrace();
        }catch (Exception e) {
            msgbox("Unknown error");
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            try (PreparedStatement doneTask = connection.prepareStatement("SELECT COUNT(*) FROM \"Tasks\" WHERE task_status = 'Done'")) {
                ResultSet resultSet = doneTask.executeQuery();
                if (resultSet.next()) {
                    done = resultSet.getInt(1);
                }
            }

            try (PreparedStatement inProgressTask = connection.prepareStatement("SELECT COUNT(*) FROM \"Tasks\" WHERE task_status = 'In Progress'")) {
                ResultSet resultSet = inProgressTask.executeQuery();
                if (resultSet.next()) {
                    inProgress = resultSet.getInt(1);
                }
            }

            try (PreparedStatement  notStartedTask= connection.prepareStatement("SELECT COUNT(*) FROM \"Tasks\" WHERE task_status = 'Not Started'")) {
                ResultSet resultSet = notStartedTask.executeQuery();
                if (resultSet.next()) {
                    notStarted = resultSet.getInt(1);
                }
            }

            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("Done", done),
                            new PieChart.Data("Not Started", notStarted),
                            new PieChart.Data("In Progress", inProgress));
            pieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), "\n", data.pieValueProperty(), " Tasks"
                            )
                    )
            );
            pieChart.setData(pieChartData);

        } catch (SQLException e) {
            msgbox("Something went wrong! check your connection");
            e.printStackTrace();
        }catch (Exception e) {
            msgbox("Unknown error");
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
        messageLabel.setStyle("""
                -fx-font-size: 11pt;
                   -fx-font-family: "Segoe UI Semibold";
                   -fx-text-fill: white;
                   -fx-opacity: 1;""".indent(1));

        alert.showAndWait();
    }
}