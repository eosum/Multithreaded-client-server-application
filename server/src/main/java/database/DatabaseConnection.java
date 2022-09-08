package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection databaseConnection;
    private static Connection connection;
    private String DB_USERNAME = "postgres";
    private String DB_PASSWORD = "bujhm";
    private String DB_URL = "jdbc:postgresql://localhost:5432/people";

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Ошибка подключения к базе данных. Попробуйте позже");
            System.exit(0);
        }
    }

    public static Connection getConnection() {
        if (databaseConnection == null) {
            databaseConnection = new DatabaseConnection();
        }
        return connection;
    }
}
