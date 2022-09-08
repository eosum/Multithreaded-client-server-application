package database;

import data.HumanBeing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;
    public DatabaseManager() {
        connection = DatabaseConnection.getConnection();
    }

    public ResultSet getFromDB() {
        String sql = "select * from character";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            return result;
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Long getLastIDFromDB() {
        String sql = "select currval(id) from character";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            return result.getLong("id");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1L;
        }
    }

    public Long add(HumanBeing element, String owner) {
        try {
            String sql = "insert into character (name, coordinate_x, coordinate_y, real_hero, has_toothpick, impact_speed, soundtrack, minutes_of_waiting, weapon_type, car, owner) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, element.getName());
            preparedStatement.setDouble(2, element.getCoordinateX());
            preparedStatement.setDouble(3, element.getCoordinateY());
            preparedStatement.setBoolean(4, element.getRealHero());
            preparedStatement.setBoolean(5, element.getHasToothpick());
            preparedStatement.setLong(6, element.getImpactSpeed());
            preparedStatement.setString(7, element.getSoundtrackName());
            preparedStatement.setInt(8, element.getMinutesOfWaiting());
            preparedStatement.setString(9, element.getWeaponType());
            preparedStatement.setString(10, element.getCar());
            preparedStatement.setString(11, owner);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            return result.getLong("id");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Integer update(HumanBeing element, Long id, String user) {
        try {
            String sql = "update character set (name, coordinate_x, coordinate_y, real_hero, has_toothpick, impact_speed, soundtrack, minutes_of_waiting, weapon_type, car) = (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) where id = ? and owner = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, element.getName());
            preparedStatement.setDouble(2, element.getCoordinateX());
            preparedStatement.setDouble(3, element.getCoordinateY());
            preparedStatement.setBoolean(4, element.getRealHero());
            preparedStatement.setBoolean(5, element.getHasToothpick());
            preparedStatement.setLong(6, element.getImpactSpeed());
            preparedStatement.setString(7, element.getSoundtrackName());
            preparedStatement.setInt(8, element.getMinutesOfWaiting());
            preparedStatement.setString(9, element.getWeaponType());
            preparedStatement.setString(10, element.getCar());
            preparedStatement.setLong(11, id);
            preparedStatement.setString(12, user);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    public Integer remove(Long id, String user) {
        try {
            String sql = "delete from character where id = ? and owner = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, user);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }


}
