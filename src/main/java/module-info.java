module com.finalproject.oopproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.web;
    requires java.sql;
    requires org.json;


    opens com.finalproject.oopproject to javafx.fxml;
    exports com.finalproject.oopproject;
}