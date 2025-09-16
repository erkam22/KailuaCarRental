import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;


public class KailuaConsoleApp {
    private static final String URL = "jdbc:mysql://localhost:3306/KailuaCarRental?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Politi123"; // ændr hvis nødvendigt

    private final Connection conn;
    private final Scanner sc = new Scanner(System.in);

    public KailuaConsoleApp(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("Forbundet til database!");
                KailuaConsoleApp app = new KailuaConsoleApp(conn);
                app.run();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver ikke fundet. Tilføj mysql-connector-j jar til classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Kan ikke forbinde til database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void run() {
        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": manageCars(); break;
                case "2": manageCustomers(); break;
                case "3": manageRentals(); break;
                case "0": System.out.println("Exit. Farvel!"); return;
                default: System.out.println("Ugyldigt valg."); break;
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n== Kailua CarRental Console ==");
        System.out.println("1) Cars (Bil)");
        System.out.println("2) Customers (Kunde)");
        System.out.println("3) Rentals (Lejekontrakt)");
        System.out.println("0) Quit");
        System.out.print("Vælg: ");
    }

    //cars
    private void manageCars() {
        while (true) {
            System.out.println("\n-- Cars Menu --");
            System.out.println("1) Insert car");
            System.out.println("2) Update car (by BilID)");
            System.out.println("3) Delete car (by BilID)");
            System.out.println("4) List all cars");
            System.out.println("5) Find cars by group");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1": insertCar(); break;
                case "2": updateCar(); break;
                case "3": deleteCar(); break;
                case "4": listAllCars(); break;
                case "5": findCarsByGroup(); break;
                case "0": return;
                default: System.out.println("Ugyldigt valg."); break;
            }
        }
    }

    private void insertCar() {
        try {
            System.out.print("Brand: "); String brand = sc.nextLine().trim();
            System.out.print("Model: "); String model = sc.nextLine().trim();
            System.out.print("FuelType: "); String fuel = sc.nextLine().trim();
            System.out.print("RegNr: "); String regnr = sc.nextLine().trim();
            System.out.print("RegAar (int): "); int year = Integer.parseInt(sc.nextLine().trim());
            System.out.print("RegMaaned (int): "); int month = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Km (int): "); int km = Integer.parseInt(sc.nextLine().trim());
            System.out.print("BilgruppeID (int, 1=Luxury,2=Family,3=Sport): "); int group = Integer.parseInt(sc.nextLine().trim());

            String sql = "INSERT INTO Bil (Brand, Model, FuelType, RegNr, RegAar, RegMaaned, Km, BilgruppeID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, brand);
                ps.setString(2, model);
                ps.setString(3, fuel.isEmpty() ? null : fuel);
                ps.setString(4, regnr);
                ps.setInt(5, year);
                ps.setInt(6, month);
                ps.setInt(7, km);
                ps.setInt(8, group);
                int r = ps.executeUpdate();
                System.out.println("Inserted rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput. Forsøg igen.");
        }
    }

    private void updateCar() {
        try {
            System.out.print("BilID to update: "); int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("New Km (int): "); int km = Integer.parseInt(sc.nextLine().trim());
            String sql = "UPDATE Bil SET Km = ? WHERE BilID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, km);
                ps.setInt(2, id);
                int r = ps.executeUpdate();
                System.out.println("Updated rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput.");
        }
    }

    private void deleteCar() {
        try {
            System.out.print("BilID to delete: "); int id = Integer.parseInt(sc.nextLine().trim());
            String sql = "DELETE FROM Bil WHERE BilID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int r = ps.executeUpdate();
                System.out.println("Deleted rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput.");
        }
    }

    private void listAllCars() {
        String sql = "SELECT b.BilID, b.Brand, b.Model, b.FuelType, b.RegNr, b.RegAar, b.RegMaaned, b.Km, bg.Navn AS Gruppe " +
                "FROM Bil b LEFT JOIN Bilgruppe bg ON b.BilgruppeID = bg.BilgruppeID";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nBilID | Brand | Model | Fuel | RegNr | RegÅr | RegMån | Km | Gruppe");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %s | %d | %d | %d | %s%n",
                        rs.getInt("BilID"), rs.getString("Brand"), rs.getString("Model"),
                        rs.getString("FuelType"), rs.getString("RegNr"),
                        rs.getInt("RegAar"), rs.getInt("RegMaaned"), rs.getInt("Km"),
                        rs.getString("Gruppe"));
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void findCarsByGroup() {
        try {
            System.out.print("Bilgruppe navn (Luxury/Family/Sport): ");
            String groupName = sc.nextLine().trim();
            String sql = "SELECT b.BilID, b.Brand, b.Model, bg.Navn AS Gruppe FROM Bil b JOIN Bilgruppe bg ON b.BilgruppeID = bg.BilgruppeID WHERE bg.Navn = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, groupName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("%d | %s | %s | %s%n", rs.getInt("BilID"), rs.getString("Brand"), rs.getString("Model"), rs.getString("Gruppe"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    // CUSTOMERS
    private void manageCustomers() {
        while (true) {
            System.out.println("\n-- Customers Menu --");
            System.out.println("1) Insert customer");
            System.out.println("2) Update customer (by KundeID)");
            System.out.println("3) Delete customer (by KundeID)");
            System.out.println("4) List all customers");
            System.out.println("5) Find by DriverLicenceNr");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1": insertCustomer(); break;
                case "2": updateCustomer(); break;
                case "3": deleteCustomer(); break;
                case "4": listAllCustomers(); break;
                case "5": findCustomerByLicence(); break;
                case "0": return;
                default: System.out.println("Ugyldigt valg."); break;
            }
        }
    }

    private void insertCustomer() {
        try {
            System.out.print("Navn: "); String navn = sc.nextLine().trim();
            System.out.print("Adresse: "); String adresse = sc.nextLine().trim();
            System.out.print("Zip: "); String zip = sc.nextLine().trim();
            System.out.print("ByNavn: "); String by = sc.nextLine().trim();
            System.out.print("Mobil: "); String mobil = sc.nextLine().trim();
            System.out.print("Telefon: "); String telefon = sc.nextLine().trim();
            System.out.print("Email: "); String email = sc.nextLine().trim();
            System.out.print("KoerekortNr: "); String kl = sc.nextLine().trim();
            System.out.print("KoerekortSiden (YYYY-MM-DD): "); String siden = sc.nextLine().trim();

            String sql = "INSERT INTO Kunde (Navn, Adresse, Zip, ByNavn, Mobil, Telefon, Email, KoerekortNr, KoerekortSiden) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, navn);
                ps.setString(2, adresse.isEmpty() ? null : adresse);
                ps.setString(3, zip.isEmpty() ? null : zip);
                ps.setString(4, by.isEmpty() ? null : by);
                ps.setString(5, mobil.isEmpty() ? null : mobil);
                ps.setString(6, telefon.isEmpty() ? null : telefon);
                ps.setString(7, email.isEmpty() ? null : email);
                ps.setString(8, kl);
                ps.setDate(9, (siden.isEmpty() ? null : Date.valueOf(LocalDate.parse(siden))));
                int r = ps.executeUpdate();
                System.out.println("Inserted rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Fejl i input. Sørg for korrekt dato-format (YYYY-MM-DD).");
        }
    }

    private void updateCustomer() {
        try {
            System.out.print("KundeID to update: "); int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("New Mobil: "); String mobil = sc.nextLine().trim();
            String sql = "UPDATE Kunde SET Mobil = ? WHERE KundeID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, mobil);
                ps.setInt(2, id);
                int r = ps.executeUpdate();
                System.out.println("Updated rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput.");
        }
    }

    private void deleteCustomer() {
        try {
            System.out.print("KundeID to delete: "); int id = Integer.parseInt(sc.nextLine().trim());
            String sql = "DELETE FROM Kunde WHERE KundeID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int r = ps.executeUpdate();
                System.out.println("Deleted rows: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput.");
        }
    }

    private void listAllCustomers() {
        String sql = "SELECT KundeID, Navn, Adresse, Zip, ByNavn, Mobil, Telefon, Email, KoerekortNr, KoerekortSiden FROM Kunde";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nKundeID | Navn | Adresse | Zip | By | Mobil | Telefon | Email | KoerekortNr | KoerekortSiden");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %s | %s | %s | %s | %s | %s%n",
                        rs.getInt("KundeID"), rs.getString("Navn"), rs.getString("Adresse"),
                        rs.getString("Zip"), rs.getString("ByNavn"), rs.getString("Mobil"),
                        rs.getString("Telefon"), rs.getString("Email"), rs.getString("KoerekortNr"),
                        rs.getDate("KoerekortSiden"));
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void findCustomerByLicence() {
        try {
            System.out.print("KoerekortNr: "); String kl = sc.nextLine().trim();
            String sql = "SELECT KundeID, Navn, Adresse FROM Kunde WHERE KoerekortNr = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, kl);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.printf("KundeID=%d, Navn=%s, Adresse=%s%n", rs.getInt("KundeID"), rs.getString("Navn"), rs.getString("Adresse"));
                    } else {
                        System.out.println("Ingen kunde med dette kørekortnummer.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    // RENTALS
    private void manageRentals() {
        while (true) {
            System.out.println("\n-- Rentals Menu --");
            System.out.println("1) Insert rental");
            System.out.println("2) Update rental (set TilDatoTid or MaxKm)");
            System.out.println("3) Delete rental (by KontraktID)");
            System.out.println("4) List all rentals");
            System.out.println("5) Find open rentals (TilDatoTid > now)");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1": insertRental(); break;
                case "2": updateRental(); break;
                case "3": deleteRental(); break;
                case "4": listAllRentals(); break;
                case "5": findOpenRentals(); break;
                case "0": return;
                default: System.out.println("Ugyldigt valg."); break;
            }
        }
    }

    private void insertRental() {
        try {
            System.out.print("KundeID: "); int kundeId = Integer.parseInt(sc.nextLine().trim());
            System.out.print("BilID: "); int bilId = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Fra (YYYY-MM-DD HH:MM:SS): "); LocalDateTime fra = LocalDateTime.parse(sc.nextLine().trim().replace(' ', 'T'));
            System.out.print("Til (YYYY-MM-DD HH:MM:SS): "); LocalDateTime til = LocalDateTime.parse(sc.nextLine().trim().replace(' ', 'T'));
            System.out.print("MaxKm (int): "); int maxKm = Integer.parseInt(sc.nextLine().trim());
            System.out.print("StartKm (int): "); int startKm = Integer.parseInt(sc.nextLine().trim());

            String sql = "INSERT INTO Lejekontrakt (KundeID, BilID, FraDatoTid, TilDatoTid, MaxKm, StartKm) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, kundeId);
                ps.setInt(2, bilId);
                ps.setTimestamp(3, Timestamp.valueOf(fra));
                ps.setTimestamp(4, Timestamp.valueOf(til));
                ps.setInt(5, maxKm);
                ps.setInt(6, startKm);
                int r = ps.executeUpdate();
                System.out.println("Inserted rentals: " + r);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Fejl i input. Tjek formater og at KundeID/BilID findes.");
        }
    }

    private void updateRental() {
        try {
            System.out.print("KontraktID to update: "); int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println("1) Update TilDatoTid  2) Update MaxKm");
            String opt = sc.nextLine().trim();
            if ("1".equals(opt)) {
                System.out.print("New Til (YYYY-MM-DD HH:MM:SS): ");
                LocalDateTime til = LocalDateTime.parse(sc.nextLine().trim().replace(' ', 'T'));
                String sql = "UPDATE Lejekontrakt SET TilDatoTid = ? WHERE KontraktID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setTimestamp(1, Timestamp.valueOf(til));
                    ps.setInt(2, id);
                    System.out.println("Updated rows: " + ps.executeUpdate());
                }
            } else if ("2".equals(opt)) {
                System.out.print("New MaxKm (int): ");
                int maxKm = Integer.parseInt(sc.nextLine().trim());
                String sql = "UPDATE Lejekontrakt SET MaxKm = ? WHERE KontraktID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, maxKm);
                    ps.setInt(2, id);
                    System.out.println("Updated rows: " + ps.executeUpdate());
                }
            } else {
                System.out.println("Ugyldigt valg.");
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Forkert input.");
        }
    }

    private void deleteRental() {
        try {
            System.out.print("KontraktID to delete: "); int id = Integer.parseInt(sc.nextLine().trim());
            String sql = "DELETE FROM Lejekontrakt WHERE KontraktID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                System.out.println("Deleted rows: " + ps.executeUpdate());
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ugyldigt talinput.");
        }
    }

    private void listAllRentals() {
        String sql = "SELECT lk.KontraktID, k.Navn AS Kunde, b.Brand AS BilBrand, b.Model AS BilModel, lk.FraDatoTid, lk.TilDatoTid, lk.MaxKm, lk.StartKm " +
                "FROM Lejekontrakt lk " +
                "LEFT JOIN Kunde k ON lk.KundeID = k.KundeID " +
                "LEFT JOIN Bil b ON lk.BilID = b.BilID";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nKontraktID | Kunde | Bil | Fra | Til | MaxKm | StartKm");
            while (rs.next()) {
                System.out.printf("%d | %s | %s %s | %s | %s | %d | %d%n",
                        rs.getInt("KontraktID"), rs.getString("Kunde"),
                        rs.getString("BilBrand"), rs.getString("BilModel"),
                        rs.getTimestamp("FraDatoTid"), rs.getTimestamp("TilDatoTid"),
                        rs.getInt("MaxKm"), rs.getInt("StartKm"));
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void findOpenRentals() {
        String sql = "SELECT KontraktID, KundeID, BilID, FraDatoTid, TilDatoTid FROM Lejekontrakt WHERE TilDatoTid > NOW()";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nKontraktID | KundeID | BilID | Fra | Til");
            while (rs.next()) {
                System.out.printf("%d | %d | %d | %s | %s%n",
                        rs.getInt("KontraktID"), rs.getInt("KundeID"), rs.getInt("BilID"),
                        rs.getTimestamp("FraDatoTid"), rs.getTimestamp("TilDatoTid"));
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }
}
