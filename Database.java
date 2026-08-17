package pawhome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL =
        "jdbc:sqlite:pawhome.db";

    public static Connection connect()
        throws SQLException {

        Connection connection =
            DriverManager.getConnection(URL);

        try (
            Statement statement =
                connection.createStatement()
        ) {
            statement.execute(
                "PRAGMA foreign_keys = ON"
            );
        }

        return connection;
    }

    public static void initialize()
        throws SQLException {

        try (
            Connection connection = connect();
            Statement statement =
                connection.createStatement()
        ) {
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL DEFAULT 'USER'" +
                ")"
            );

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS pets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "breed TEXT NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "gender TEXT NOT NULL, " +
                "health TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'AVAILABLE'" +
                ")"
            );

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS applications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "pet_id INTEGER NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "home_type TEXT NOT NULL, " +
                "has_pets TEXT NOT NULL, " +
                "application_date TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'PENDING', " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(pet_id) REFERENCES pets(id)" +
                ")"
            );
        }

        seedData();
    }

    private static void seedData()
        throws SQLException {

        try (Connection connection = connect()) {
            addAdministrator(connection);
            addSamplePets(connection);
        }
    }

    private static void addAdministrator(
        Connection connection
    ) throws SQLException {

        String checkSql =
            "SELECT COUNT(*) FROM users " +
            "WHERE username = ?";

        try (
            PreparedStatement checkStatement =
                connection.prepareStatement(checkSql)
        ) {
            checkStatement.setString(1, "admin");

            ResultSet result =
                checkStatement.executeQuery();

            if (
                result.next() &&
                result.getInt(1) == 0
            ) {
                String insertSql =
                    "INSERT INTO users(" +
                    "full_name, phone, username, " +
                    "password, role" +
                    ") VALUES(?,?,?,?,?)";

                try (
                    PreparedStatement insertStatement =
                        connection.prepareStatement(
                            insertSql
                        )
                ) {
                    insertStatement.setString(
                        1,
                        "System Administrator"
                    );
                    insertStatement.setString(
                        2,
                        "0123456789"
                    );
                    insertStatement.setString(
                        3,
                        "admin"
                    );
                    insertStatement.setString(
                        4,
                        "admin123"
                    );
                    insertStatement.setString(
                        5,
                        "ADMIN"
                    );

                    insertStatement.executeUpdate();
                }
            }
        }
    }

    private static void addSamplePets(
        Connection connection
    ) throws SQLException {

        String checkSql =
            "SELECT COUNT(*) FROM pets";

        try (
            Statement checkStatement =
                connection.createStatement();
            ResultSet result =
                checkStatement.executeQuery(checkSql)
        ) {
            if (
                result.next() &&
                result.getInt(1) == 0
            ) {
                String insertSql =
                    "INSERT INTO pets(" +
                    "name, type, breed, age, " +
                    "gender, health, status" +
                    ") VALUES(?,?,?,?,?,?,?)";

                try (
                    PreparedStatement insertStatement =
                        connection.prepareStatement(
                            insertSql
                        )
                ) {
                    insertPet(
                        insertStatement,
                        "Milo",
                        "Dog",
                        "Golden Retriever",
                        3,
                        "Male",
                        "Healthy and vaccinated"
                    );

                    insertPet(
                        insertStatement,
                        "Luna",
                        "Cat",
                        "Domestic Shorthair",
                        2,
                        "Female",
                        "Healthy and vaccinated"
                    );

                    insertPet(
                        insertStatement,
                        "Coco",
                        "Rabbit",
                        "Holland Lop",
                        1,
                        "Female",
                        "Healthy"
                    );
                }
            }
        }
    }

    private static void insertPet(
        PreparedStatement statement,
        String name,
        String type,
        String breed,
        int age,
        String gender,
        String health
    ) throws SQLException {

        statement.setString(1, name);
        statement.setString(2, type);
        statement.setString(3, breed);
        statement.setInt(4, age);
        statement.setString(5, gender);
        statement.setString(6, health);
        statement.setString(7, "AVAILABLE");

        statement.executeUpdate();
    }
}

record User(
    int id,
    String fullName,
    String phone,
    String username,
    String role
) {
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}

interface Refreshable {
    void refreshData();
}