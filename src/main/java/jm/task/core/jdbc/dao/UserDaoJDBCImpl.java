package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private final Connection connection = Util.ConnectionDB();

    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {
        String command= "CREATE TABLE users (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(30), lastName VARCHAR(30)," +
                "age TINYINT NOT NULL, PRIMARY KEY (id))";
        try (Statement statement  = connection.createStatement()) {
            statement.executeUpdate(command);
        } catch (SQLException e) {
            System.out.println("Создать таблицу не удалось. Такая таблица уже существует!");
        }
    }

    public void dropUsersTable() {
        try(Statement statement = connection.createStatement()){
            statement.execute("DROP TABLE users");
        } catch (SQLException e) {
            System.out.println("Удаление таблицыне не выполнено. Такой таблицы не существует!");
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO users (name,lastname,age) VALUE (?, ?, ?)")){
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();

            System.out.println("User с именем - " + name + " добавлен в базу данных");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        }
    }

    public void removeUserById(long id) {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM users WHERE id= ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users")){
            while (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getByte("age"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void cleanUsersTable() {
        try(Statement statement = connection.createStatement()){
            statement.execute("DELETE FROM users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
