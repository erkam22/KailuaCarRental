import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/KailuaCarRental?serverTimezone=UTC",
                    "root",
                    "Politi123"
            );

            System.out.println("Forbundet til database!");
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
