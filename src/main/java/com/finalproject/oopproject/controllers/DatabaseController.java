package com.finalproject.oopproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

public class DatabaseController {
private static final String DB_URL = "jdbc:mysql://localhost:3306/weatherdb";
private static final String USER = "root";
private static final String PASS = "edward1234";

public static List<String> getMatchingCities(String query) {
    ObservableList<String> results = FXCollections.observableArrayList();
    String sql = "SELECT name FROM cities WHERE name LIKE ? LIMIT 10";

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + query + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            results.add(rs.getString("name"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return results;
    }

}
