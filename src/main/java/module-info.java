module es.ies.puerto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens es.puerto.juego1 to javafx.fxml;

    exports es.puerto.juego1;
    exports es.puerto.juego1.controller;
    exports es.puerto.juego1.config;
    exports es.puerto.juego1.model;

    opens es.puerto.juego1.controller to javafx.fxml;
    opens es.puerto.juego1.model to com.fasterxml.jackson.databind,
            org.junit.platform.commons, org.mockito;
}