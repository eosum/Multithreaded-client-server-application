package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserExist {
    private static Connection connection = DatabaseConnection.getConnection();

    private UserExist() {
    }

    public static String register(String user, String password) {
        String sql = "select * from users_info where login = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) return "Пользователь с таким логином уже существует";

            sql = "insert into users_info (login, password) values (?, md5(?))";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            return "Успешно";
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Ошибка подключения к БД. Повторите запрос позднее";
        }
    }

    public static String login(String user, String password) {
        String sql = "select * from users_info where login = ? and password = md5(?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) return "Успешно";
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Ошибка подключения к БД. Повторите запрос позднее";
        }
        return "Неверный логин или пароль";
    }

}
