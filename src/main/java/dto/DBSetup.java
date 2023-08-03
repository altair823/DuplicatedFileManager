package dto;

import java.sql.Connection;
import java.sql.SQLException;

public class DBSetup {

    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASSWORD;

    public DBSetup(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public Connection createConnection() throws SQLException, ClassNotFoundException {
        return java.sql.DriverManager.getConnection(
                "jdbc:mysql://" +
                DB_URL +
                "/duptest?serverTimezone=UTC&useSSL=false",
                DB_USER,
                DB_PASSWORD);
    }
}
