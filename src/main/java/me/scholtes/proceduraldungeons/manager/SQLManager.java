package me.scholtes.proceduraldungeons.manager;

import java.sql.*;

public class SQLManager {

    public SQLManager() {
        createTable();
    }

    private void createTable() {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username VARCHAR(24) NOT NULL UNIQUE," +
                            "email TEXT NOT NULL," +
                            "password TEXT NOT NULL UNIQUE," +
                            "salt TEXT NOT NULL," +
                            "verified BOOLEAN NOT NULL," +
                            "games_played INTEGER NOT NULL," +
                            "games_won INTEGER NOT NULL," +
                            "total_kills INTEGER NOT NULL);"
            );

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
    }


    public Connection getConnection() {
        String url = "jdbc:sqlite:ProceduralDungeons.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
