package com.splat.database;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

// Работа с БД.
public class DBManager {

    /*
    Метод инициализации текующего соединения.
    Получаем настройки из файла db.properties.
     */
    private static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties props = new Properties();
        props.load(DBManager.class.getClassLoader().getResourceAsStream("db.properties"));
        String driver = props.getProperty("driver");
        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String pass = props.getProperty("pass");
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    /*
    Метод получения значения value по указанному id.
     */
    public Long getAmount(Integer id) throws SQLException, IOException, ClassNotFoundException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String query = "select value from accounts where id = " + id;
        ResultSet resultSet = statement.executeQuery(query);
        Long value;
        //Если нет счета с таким id, возвращаем 0.
        if (resultSet.next())
        {
            value = resultSet.getLong("value");
        }
        else
        {
            value = 0L;
        }
        statement.close();
        connection.close();
        return value;
    }

    /*
    Метод добавления/обновления value по id.
     */
    public void addAmount(Integer id, Long value) throws IOException, ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        Statement statement;
        String querySelect = "select value from accounts where id = " + id;
        String queryInsert = "insert into accounts (id, value) values (" + id + "," + value + ")";
        String queryUpdate = "update accounts SET value = value + " + value + " where id = " + id;
        //Выключаем настройку для автоматической транзакции.
        connection.setAutoCommit(false);
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querySelect);
            // Если в таблице есть счет с пришедшим id, то обновляем текущий счет.
            // Иначе создаем новый и записываем туда value.
            if (resultSet.next()) {
                statement.executeUpdate(queryUpdate);
            }
            else
            {
                statement.executeUpdate(queryInsert);
            }
            //В случае успешной транзакции сохраняем изменения.
            connection.commit();
        }
        catch (SQLException e)
        {
            //В случае неуспешной транзакции делаем откат.
            connection.rollback();
            throw e;
        }
        finally
        {
            connection.close();
        }
    }

}
