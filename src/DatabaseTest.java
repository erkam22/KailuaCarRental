import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // Valgfrit i nyere Java, sikrer driveren findes
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Forbind til MySQL-databasen
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/KailuaCarRental?serverTimezone=UTC",
                    "root",          // Din MySQL-bruger
                    "Politi123"      // Din MySQL-adgangskode
            );

            System.out.println("Forbundet til database!");
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
