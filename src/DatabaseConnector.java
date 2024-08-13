import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static String URL = "jdbc:postgresql://localhost:5432/tabelinha";
    private static String USER = "Teste";
    private static String USER_PASSWORD = "123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, USER_PASSWORD);
    }
}
