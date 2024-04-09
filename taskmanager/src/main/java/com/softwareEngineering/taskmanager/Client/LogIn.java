package com.softwareEngineering.taskmanager.Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;

public class LogIn extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
    private static final String user = "taskmanager_admin";
    private static final String password = "iLUqbipQL";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // GridPane for layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.setStyle("-fx-background-color: #02030A");

        // First Name Label and Textfield
        Label IDNameLabel = new Label("ID:");
        TextField IDNameField = new TextField();
        grid.add(IDNameLabel, 0, 0);
        grid.add(IDNameField, 1, 0);

        // Password textfield
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField(); // Use PasswordField for password entry
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        // Login Button
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 3);
       

        // Event handling for the login button
        loginButton.setOnAction(e -> {
            String ID = IDNameField.getText();
            String password = passwordField.getText();

            // login message

            // Check login credentials
            if (validateLogin(ID, password)) {

                // Show a welcome message
                showWelcomeMessage(ID);


                TaskTableView taskTableView = new TaskTableView(Integer.parseInt(ID));
                // If credentials are valid, show the initial table view
                taskTableView.showTaskTableView();
            } else {
                // Otherwise, display an error message or handle accordingly
                System.out.println("Invalid login credentials");
                showAlert("Invalid Login", "Invalid username or password. Please try again.");
            }

        });


        // Scene
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        // Set the scene to the stage
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    // Method to validate login credentials
    private boolean validateLogin(String id, String password) {
        try (Connection connection = connect()) {
            if (connection != null) {
                String sql = "SELECT * FROM \"Accounts\" WHERE (account_id = ? AND password IN(?));";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, Integer.parseInt(id));
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    return resultSet.next(); // If there is a match, return true
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Default to false if an exception occurs
    }

    private void showWelcomeMessage(String id) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText("Welcome, User " + id + "!");
        alert.setContentText("You have successfully logged in.");
        alert.showAndWait();
    }

    // Method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
