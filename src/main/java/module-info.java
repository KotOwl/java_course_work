module ua.lpnu.coffevan {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.xerial.sqlitejdbc;
    requires transitive java.sql;

    opens ua.lpnu.coffevan to javafx.fxml;
    opens ua.lpnu.coffevan.ui to javafx.fxml;
    opens ua.lpnu.coffevan.model to javafx.base;

    exports ua.lpnu.coffevan;
    exports ua.lpnu.coffevan.model;
    exports ua.lpnu.coffevan.service;
    exports ua.lpnu.coffevan.repository;
    exports ua.lpnu.coffevan.controller;
    exports ua.lpnu.coffevan.dto;
    exports ua.lpnu.coffevan.ui;
    exports ua.lpnu.coffevan.util;
}
