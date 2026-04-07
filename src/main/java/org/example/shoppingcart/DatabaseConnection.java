package org.example.shoppingcart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  // private static final String URL = "jdbc:mariadb://localhost:3306/shopping_cart_localization";
    private static final String URL = "jdbc:mariadb://host.docker.internal:3306/shopping_cart_localization";

    private static final String USER = "appuser";        // your user
    private static final String PASS = "password";    // your password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}