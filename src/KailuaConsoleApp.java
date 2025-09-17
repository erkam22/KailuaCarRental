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
                new KailuaConsoleApp(conn).run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        while (true) {
            System.out.println("\n== Kailua CarRental Console ==");
            System.out.println("1) Cars (Bil)");
            System.out.println("2) Customers (Kunde)");
            System.out.println("3) Rentals (Lejekontrakt)");
            System.out.println("0) Quit");
            System.out.print("Vælg: ");
            switch (sc.nextLine().trim()) {
                case "1": manageCars(); break;
                case "2": manageCustomers(); break;
                case "3": manageRentals(); break;
                case "0": return;
                default: System.out.println("Ugyldigt valg."); break;
            }
        }
    }

    // === Hjælpemetoder ===
    private void executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            System.out.println("Affected rows: " + ps.executeUpdate());
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void printQuery(String sql, Object... params) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        System.out.print(rs.getString(i));
                        if (i < cols) System.out.print(" | ");
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    // === Cars ===
    private void manageCars() {
        while (true) {
            System.out.println("\n-- Cars Menu --");
            System.out.println("1) Insert car");
            System.out.println("2) Update Km");
            System.out.println("3) Delete car");
            System.out.println("4) List all cars");
            System.out.println("5) Find cars by group");
            System.out.println("0) Back");
            switch (sc.nextLine().trim()) {
                case "1": insertCar(); break;
                case "2": updateCar(); break;
                case "3": deleteCar(); break;
                case "4": listAllCars(); break;
                case "5": findCarsByGroup(); break;
                case "0": return;
            }
        }
    }

    private void insertCar() {
        try {
            System.out.print("Brand: "); String brand = sc.nextLine();
            System.out.print("Model: "); String model = sc.nextLine();
            System.out.print("FuelType: "); String fuel = sc.nextLine();
            System.out.print("RegNr: "); String regnr = sc.nextLine();
            System.out.print("RegAar: "); int year = Integer.parseInt(sc.nextLine());
            System.out.print("RegMaaned: "); int month = Integer.parseInt(sc.nextLine());
            System.out.print("Km: "); int km = Integer.parseInt(sc.nextLine());
            System.out.print("BilgruppeID: "); int group = Integer.parseInt(sc.nextLine());
            executeUpdate("INSERT INTO Bil (Brand, Model, FuelType, RegNr, RegAar, RegMaaned, Km, BilgruppeID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    brand, model, fuel.isEmpty()?null:fuel, regnr, year, month, km, group);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void updateCar() {
        try {
            System.out.print("BilID: "); int id = Integer.parseInt(sc.nextLine());
            System.out.print("New Km: "); int km = Integer.parseInt(sc.nextLine());
            executeUpdate("UPDATE Bil SET Km = ? WHERE BilID = ?", km, id);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void deleteCar() {
        try {
            System.out.print("BilID: "); int id = Integer.parseInt(sc.nextLine());
            executeUpdate("DELETE FROM Bil WHERE BilID = ?", id);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void listAllCars() {
        printQuery("SELECT b.BilID, b.Brand, b.Model, b.FuelType, b.RegNr, b.RegAar, b.RegMaaned, b.Km, bg.Navn AS Gruppe " +
                "FROM Bil b LEFT JOIN Bilgruppe bg ON b.BilgruppeID = bg.BilgruppeID");
    }

    private void findCarsByGroup() {
        System.out.print("Bilgruppe navn: "); String g = sc.nextLine();
        printQuery("SELECT b.BilID, b.Brand, b.Model, bg.Navn FROM Bil b JOIN Bilgruppe bg ON b.BilgruppeID = bg.BilgruppeID WHERE bg.Navn = ?", g);
    }

    // === Customers ===
    private void manageCustomers() {
        while (true) {
            System.out.println("\n-- Customers Menu --");
            System.out.println("1) Insert customer");
            System.out.println("2) Update mobil");
            System.out.println("3) Delete customer");
            System.out.println("4) List all customers");
            System.out.println("5) Find by licence");
            System.out.println("0) Back");
            switch (sc.nextLine().trim()) {
                case "1": insertCustomer(); break;
                case "2": updateCustomer(); break;
                case "3": deleteCustomer(); break;
                case "4": listAllCustomers(); break;
                case "5": findCustomerByLicence(); break;
                case "0": return;
            }
        }
    }

    private void insertCustomer() {
        try {
            System.out.print("Navn: "); String navn = sc.nextLine();
            System.out.print("Adresse: "); String adr = sc.nextLine();
            System.out.print("Zip: "); String zip = sc.nextLine();
            System.out.print("By: "); String by = sc.nextLine();
            System.out.print("Mobil: "); String mobil = sc.nextLine();
            System.out.print("Telefon: "); String tlf = sc.nextLine();
            System.out.print("Email: "); String email = sc.nextLine();
            System.out.print("KoerekortNr: "); String kl = sc.nextLine();
            System.out.print("KoerekortSiden (YYYY-MM-DD): "); String siden = sc.nextLine();
            executeUpdate("INSERT INTO Kunde (Navn, Adresse, Zip, ByNavn, Mobil, Telefon, Email, KoerekortNr, KoerekortSiden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    navn, adr.isEmpty()?null:adr, zip.isEmpty()?null:zip, by.isEmpty()?null:by,
                    mobil.isEmpty()?null:mobil, tlf.isEmpty()?null:tlf, email.isEmpty()?null:email,
                    kl, siden.isEmpty()?null:Date.valueOf(LocalDate.parse(siden)));
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void updateCustomer() {
        try {
            System.out.print("KundeID: "); int id = Integer.parseInt(sc.nextLine());
            System.out.print("New Mobil: "); String mobil = sc.nextLine();
            executeUpdate("UPDATE Kunde SET Mobil = ? WHERE KundeID = ?", mobil, id);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void deleteCustomer() {
        try {
            System.out.print("KundeID: "); int id = Integer.parseInt(sc.nextLine());
            executeUpdate("DELETE FROM Kunde WHERE KundeID = ?", id);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void listAllCustomers() {
        printQuery("SELECT KundeID, Navn, Adresse, Zip, ByNavn, Mobil, Telefon, Email, KoerekortNr, KoerekortSiden FROM Kunde");
    }

    private void findCustomerByLicence() {
        System.out.print("KoerekortNr: "); String kl = sc.nextLine();
        printQuery("SELECT KundeID, Navn, Adresse FROM Kunde WHERE KoerekortNr = ?", kl);
    }

    // === Rentals ===
    private void manageRentals() {
        while (true) {
            System.out.println("\n-- Rentals Menu --");
            System.out.println("1) Insert rental");
            System.out.println("2) Update rental");
            System.out.println("3) Delete rental");
            System.out.println("4) List all rentals");
            System.out.println("5) Find open rentals");
            System.out.println("0) Back");
            switch (sc.nextLine().trim()) {
                case "1": insertRental(); break;
                case "2": updateRental(); break;
                case "3": deleteRental(); break;
                case "4": listAllRentals(); break;
                case "5": findOpenRentals(); break;
                case "0": return;
            }
        }
    }

    private void insertRental() {
        try {
            System.out.print("KundeID: "); int kid = Integer.parseInt(sc.nextLine());
            System.out.print("BilID: "); int bid = Integer.parseInt(sc.nextLine());
            System.out.print("Fra (YYYY-MM-DD HH:MM:SS): "); LocalDateTime fra = LocalDateTime.parse(sc.nextLine().replace(" ","T"));
            System.out.print("Til (YYYY-MM-DD HH:MM:SS): "); LocalDateTime til = LocalDateTime.parse(sc.nextLine().replace(" ","T"));
            System.out.print("MaxKm: "); int maxKm = Integer.parseInt(sc.nextLine());
            System.out.print("StartKm: "); int startKm = Integer.parseInt(sc.nextLine());
            executeUpdate("INSERT INTO Lejekontrakt (KundeID, BilID, FraDatoTid, TilDatoTid, MaxKm, StartKm) VALUES (?, ?, ?, ?, ?, ?)",
                    kid, bid, Timestamp.valueOf(fra), Timestamp.valueOf(til), maxKm, startKm);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void updateRental() {
        try {
            System.out.print("KontraktID: "); int id = Integer.parseInt(sc.nextLine());
            System.out.println("1) Update TilDatoTid  2) Update MaxKm");
            switch (sc.nextLine()) {
                case "1":
                    System.out.print("New Til: ");
                    LocalDateTime til = LocalDateTime.parse(sc.nextLine().replace(" ","T"));
                    executeUpdate("UPDATE Lejekontrakt SET TilDatoTid=? WHERE KontraktID=?", Timestamp.valueOf(til), id);
                    break;
                case "2":
                    System.out.print("New MaxKm: "); int maxKm = Integer.parseInt(sc.nextLine());
                    executeUpdate("UPDATE Lejekontrakt SET MaxKm=? WHERE KontraktID=?", maxKm, id);
                    break;
            }
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void deleteRental() {
        try {
            System.out.print("KontraktID: "); int id = Integer.parseInt(sc.nextLine());
            executeUpdate("DELETE FROM Lejekontrakt WHERE KontraktID = ?", id);
        } catch (Exception e) { System.out.println("Fejl i input."); }
    }

    private void listAllRentals() {
        printQuery("SELECT lk.KontraktID, k.Navn, b.Brand, b.Model, lk.FraDatoTid, lk.TilDatoTid, lk.MaxKm, lk.StartKm " +
                "FROM Lejekontrakt lk LEFT JOIN Kunde k ON lk.KundeID=k.KundeID " +
                "LEFT JOIN Bil b ON lk.BilID=b.BilID");
    }

    private void findOpenRentals() {
        printQuery("SELECT KontraktID, KundeID, BilID, FraDatoTid, TilDatoTid FROM Lejekontrakt WHERE TilDatoTid > NOW()");
    }
}
