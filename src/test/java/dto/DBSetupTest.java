package dto;

import jdk.jfr.Category;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

@Category("localDBTest")
public class DBSetupTest {
    @Test
    void createConnectionTest() throws SQLException, ClassNotFoundException {
        DBSetup dbSetup = new DBSetup(
                "192.168.0.26",
                "altair823",
                "08230734eric!@"
        );
        Connection connection = dbSetup.createConnection();
        connection.close();
    }
}
