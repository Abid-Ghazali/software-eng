package com.softwareEngineering.taskmanager.Admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("Panel.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 940, 380);
        stage.setTitle("Admin Panel");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("admin-panel.png")));
        stage.setScene(scene);

        // To make the window fixed, and not resizable
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}