module com.example.addressbook {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.web;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires java.desktop;
    requires password4j;
    requires jdk.compiler;

    opens com.example.addressbook to javafx.fxml;
    exports com.example.addressbook;
    exports com.example.addressbook.controller;
    opens com.example.addressbook.controller to javafx.fxml;
    exports com.example.addressbook.model;
    opens com.example.addressbook.model to javafx.fxml;
    exports com.example.addressbook.helper;
    opens com.example.addressbook.helper to javafx.fxml;
    exports com.example.addressbook.service;
    opens com.example.addressbook.service to javafx.fxml;
}