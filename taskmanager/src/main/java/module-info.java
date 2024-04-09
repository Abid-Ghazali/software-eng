module com.softwareEngineering.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.softwareEngineering.taskmanager.Admin to javafx.fxml;
    exports com.softwareEngineering.taskmanager.Admin;

    opens com.softwareEngineering.taskmanager.Client to javafx.fxml;
    exports com.softwareEngineering.taskmanager.Client;
}