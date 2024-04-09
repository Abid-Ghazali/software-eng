package com.softwareEngineering.taskmanager.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button btnexit;
    public Button btntasks;
    public Button btnkontos;
    public Button btnchart;

    @FXML
    private Button btnOverview;

    @FXML
    private Pane pnlKontos;

    @FXML
    private Pane pnlTasks;

    @FXML
    private Pane pnlOverview;

    @FXML
    private Pane pnlChart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFXML("PnlOverview.fxml", pnlOverview);
        pnlOverview.setVisible(true);
        pnlTasks.setVisible(false);
        pnlKontos.setVisible(false);
        pnlChart.setVisible(false);
    }


    public void handleClicks(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnOverview) {
            loadFXML("PnlOverview.fxml", pnlOverview);
            pnlOverview.setVisible(true);
            pnlTasks.setVisible(false);
            pnlChart.setVisible(false);
            pnlKontos.setVisible(false);
        }

        if (actionEvent.getSource() == btnkontos) {
            loadFXML("PnlKontos.fxml", pnlKontos);

            pnlOverview.setVisible(false);
            pnlTasks.setVisible(false);
            pnlChart.setVisible(false);
            pnlKontos.setVisible(true);
        }
        if (actionEvent.getSource() == btntasks) {
            loadFXML("PnlTasks.fxml", pnlTasks);
            pnlOverview.setVisible(false);
            pnlChart.setVisible(false);
            pnlTasks.setVisible(true);
            pnlKontos.setVisible(false);
        }
        if (actionEvent.getSource() == btnchart) {
            loadFXML("PnlChart.fxml", pnlChart);
            pnlOverview.setVisible(false);
            pnlChart.setVisible(true);
            pnlTasks.setVisible(false);
            pnlKontos.setVisible(false);
        }
        if (actionEvent.getSource() == btnexit) {
            System.exit(0);
        }
    }

    private void loadFXML(String fxmlFileName, Pane pane) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = fxmlLoader.load();
            pane.getChildren().add(root);
            root.prefWidth(793.0);
            root.prefHeight(554.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}