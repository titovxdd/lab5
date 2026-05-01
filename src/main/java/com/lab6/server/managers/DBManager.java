package com.lab6.server.managers;

import com.lab6.common.models.*;
import com.lab6.server.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Properties;
import java.util.PriorityQueue;
import com.lab6.common.Sup.*;

public class DBManager {
    private static volatile DBManager instance;
    private static Connection connection;

    private DBManager() {
        try (FileInputStream input = new FileInputStream("dbconfig.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            createTables();
            Server.logger.info("Connected to database successfully");
        } catch (SQLException | IOException e) {
            Server.logger.severe("Failed to connect to database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    private void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR(50) UNIQUE NOT NULL,
                         password_hash VARCHAR(96) NOT NULL,
                         created_at TIMESTAMP DEFAULT NOW()
                     );
        """;

        String createCoordinatesTable = """
            CREATE TABLE IF NOT EXISTS coordinates (
                id SERIAL PRIMARY KEY,
                x BIGINT NOT NULL,
                y REAL NOT NULL
            )
        """;

        String createPersonTable = """
            CREATE TABLE IF NOT EXISTS person (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                birthday DATE NOT NULL,
                eye_color VARCHAR(20) NOT NULL
            )
        """;

        String createMusicBandsTable = """
            CREATE TABLE IF NOT EXISTS music_bands (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                coordinates_id INTEGER NOT NULL REFERENCES coordinates(id) ON DELETE CASCADE,
                creation_date DATE NOT NULL,
                number_of_participants BIGINT NOT NULL CHECK (number_of_participants > 0),
                singles_count BIGINT NOT NULL CHECK (singles_count > 0),
                description VARCHAR(255),
                genre VARCHAR(50),
                front_man_id INTEGER NOT NULL REFERENCES person(id) ON DELETE CASCADE,
                user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                created_at TIMESTAMP DEFAULT NOW()
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCoordinatesTable);
            stmt.execute(createPersonTable);
            stmt.execute(createMusicBandsTable);
            Server.logger.info("Database tables created/verified");
        } catch (SQLException e) {
            Server.logger.severe("Failed to create tables: " + e.getMessage());
        }
    }

    // ==================== РАБОТА С ПОЛЬЗОВАТЕЛЯМИ ====================

    public ExecutionStatus addUser(Pair<String, String> user) {
        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try {
            // Хэшируем пароль (без соли)
            String hashedPassword = PasswordHasher.hash(user.getSecond());

            try (PreparedStatement p = connection.prepareStatement(query)) {
                p.setString(1, user.getFirst());
                p.setString(2, hashedPassword);
                p.executeUpdate();
            }

            Server.logger.info("User registered: " + user.getFirst());
            return new ExecutionStatus(true, "User registered successfully!");

        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint")) {
                return new ExecutionStatus(false, "Username already exists!");
            }
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus updateUserPermissions(String username, String permission) {
        String query = "UPDATE users SET permissions = ? WHERE username = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, permission);
            p.setString(2, username);
            int affectedRows = p.executeUpdate();
            if (affectedRows > 0) {
                Server.logger.info("User permissions updated: " + username + " -> " + permission);
                return new ExecutionStatus(true, "User permissions updated successfully!");
            } else {
                return new ExecutionStatus(false, "User not found!");
            }
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    /**
     * Проверка прав пользователя
     * @return ExecutionStatus с permissions (USER или ADMIN)
     */
    public ExecutionStatus checkUserPermission(Pair<String, String> user) {
        String query = "SELECT permissions FROM users WHERE username = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, user.getFirst());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String permission = rs.getString("permissions");
                return new ExecutionStatus(true, permission);
            } else {
                return new ExecutionStatus(false, "User not found!");
            }
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus checkPassword(Pair<String, String> user) {
        String query = "SELECT password_hash FROM users WHERE username = ?";

        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, user.getFirst());
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Хэшируем введённый пароль и сравниваем
                String inputHash = PasswordHasher.hash(user.getSecond());

                if (inputHash.equals(storedHash)) {
                    Server.logger.info("User logged in: " + user.getFirst());
                    return new ExecutionStatus(true, "Login successful!");
                } else {
                    return new ExecutionStatus(false, "Invalid password!");
                }
            } else {
                return new ExecutionStatus(false, "User not found!");
            }
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public Integer getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            Server.logger.severe("Error getting user ID: " + e.getMessage());
        }
        return null;
    }

    public boolean isOwner(Long bandId, String username) {
        String query = "SELECT user_id FROM music_bands WHERE id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setLong(1, bandId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                int ownerId = rs.getInt("user_id");
                Integer userId = getUserId(username);
                return userId != null && ownerId == userId;
            }
        } catch (SQLException e) {
            Server.logger.severe("Error checking ownership: " + e.getMessage());
        }
        return false;
    }

    // ==================== РАБОТА С КОЛЛЕКЦИЕЙ ====================

    private int insertCoordinates(Coordinates coordinates) throws SQLException {
        String query = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setLong(1, coordinates.getX());
            p.setFloat(2, coordinates.getY());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Failed to insert coordinates");
        }
    }

    private int insertPerson(Person person) throws SQLException {
        String query = "INSERT INTO person (name, birthday, eye_color) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, person.getName());
            p.setDate(2, Date.valueOf(person.getBirthday()));
            p.setString(3, person.getEyeColor().name());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Failed to insert person");
        }
    }

    public ExecutionStatus addMusicBand(MusicBand band, Pair<String, String> user) {
        Integer userId = getUserId(user.getFirst());
        if (userId == null) {
            return new ExecutionStatus(false, "User not found!");
        }

        try {
            int coordinatesId = insertCoordinates(band.getCoordinates());
            int frontManId = insertPerson(band.getFrontMan());

            String query = """
                INSERT INTO music_bands 
                (name, coordinates_id, creation_date, number_of_participants, 
                 singles_count, description, genre, front_man_id, user_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
            """;

            try (PreparedStatement p = connection.prepareStatement(query)) {
                p.setString(1, band.getName());
                p.setInt(2, coordinatesId);
                p.setDate(3, Date.valueOf(band.getCreationDate()));
                p.setLong(4, band.getNumberOfParticipants());
                p.setLong(5, band.getSinglesCount());
                p.setString(6, band.getDescription());
                p.setString(7, band.getGenre() != null ? band.getGenre().name() : null);
                p.setInt(8, frontManId);
                p.setInt(9, userId);

                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    long newId = rs.getLong("id");
                    band.updateId(newId);
                    Server.logger.info("Band added: " + band.getName() + " by " + user.getFirst());
                    return new ExecutionStatus(true, String.valueOf(newId));
                }
                return new ExecutionStatus(false, "Failed to add band");
            }

        } catch (SQLException e) {
            Server.logger.severe("Error adding band: " + e.getMessage());
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus updateMusicBand(MusicBand band, Pair<String, String> user) {
        if (!isOwner(band.getId(), user.getFirst())) {
            return new ExecutionStatus(false, "You don't own this band!");
        }

        try {
            String selectQuery = "SELECT coordinates_id, front_man_id FROM music_bands WHERE id = ?";
            int coordinatesId;
            int frontManId;

            try (PreparedStatement p = connection.prepareStatement(selectQuery)) {
                p.setLong(1, band.getId());
                ResultSet rs = p.executeQuery();
                if (!rs.next()) {
                    return new ExecutionStatus(false, "Band not found!");
                }
                coordinatesId = rs.getInt("coordinates_id");
                frontManId = rs.getInt("front_man_id");
            }

            String updateCoords = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
            try (PreparedStatement p = connection.prepareStatement(updateCoords)) {
                p.setLong(1, band.getCoordinates().getX());
                p.setFloat(2, band.getCoordinates().getY());
                p.setInt(3, coordinatesId);
                p.executeUpdate();
            }

            String updatePerson = "UPDATE person SET name = ?, birthday = ?, eye_color = ? WHERE id = ?";
            try (PreparedStatement p = connection.prepareStatement(updatePerson)) {
                p.setString(1, band.getFrontMan().getName());
                p.setDate(2, Date.valueOf(band.getFrontMan().getBirthday()));
                p.setString(3, band.getFrontMan().getEyeColor().name());
                p.setInt(4, frontManId);
                p.executeUpdate();
            }

            String updateBand = """
                UPDATE music_bands 
                SET name = ?, number_of_participants = ?, singles_count = ?, 
                    description = ?, genre = ?
                WHERE id = ?
            """;

            try (PreparedStatement p = connection.prepareStatement(updateBand)) {
                p.setString(1, band.getName());
                p.setLong(2, band.getNumberOfParticipants());
                p.setLong(3, band.getSinglesCount());
                p.setString(4, band.getDescription());
                p.setString(5, band.getGenre() != null ? band.getGenre().name() : null);
                p.setLong(6, band.getId());
                p.executeUpdate();
            }

            Server.logger.info("Band updated: " + band.getId() + " by " + user.getFirst());
            return new ExecutionStatus(true, "Band updated successfully!");

        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus removeById(Long id, Pair<String, String> user) {
        if (!isOwner(id, user.getFirst())) {
            return new ExecutionStatus(false, "You don't own this band!");
        }

        String query = "DELETE FROM music_bands WHERE id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setLong(1, id);
            int affected = p.executeUpdate();
            if (affected > 0) {
                Server.logger.info("Band removed: " + id + " by " + user.getFirst());
                return new ExecutionStatus(true, "Band removed successfully!");
            }
            return new ExecutionStatus(false, "Band not found!");
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus clear(Pair<String, String> user) {
        Integer userId = getUserId(user.getFirst());
        if (userId == null) {
            return new ExecutionStatus(false, "User not found!");
        }

        String query = "DELETE FROM music_bands WHERE user_id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, userId);
            int deleted = p.executeUpdate();
            Server.logger.info("Cleared " + deleted + " bands for user " + user.getFirst());
            return new ExecutionStatus(true, "Cleared " + deleted + " bands!");
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    /**
     * Загрузка коллекции в PriorityQueue
     */
    public ExecutionStatus loadCollection(PriorityQueue<MusicBand> collection) {
        String query = """
            SELECT 
                b.id, b.name, b.creation_date, b.number_of_participants,
                b.singles_count, b.description, b.genre,
                c.x, c.y,
                p.name as front_man_name, p.birthday, p.eye_color,
                u.username as owner_name
            FROM music_bands b
            JOIN coordinates c ON b.coordinates_id = c.id
            JOIN person p ON b.front_man_id = p.id
            JOIN users u ON b.user_id = u.id
            ORDER BY b.id
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            collection.clear();
            while (rs.next()) {
                Coordinates coordinates = new Coordinates(
                        rs.getLong("x"),
                        rs.getFloat("y")
                );

                Person frontMan = new Person(
                        rs.getString("front_man_name"),
                        rs.getDate("birthday").toLocalDate(),
                        Color.valueOf(rs.getString("eye_color"))
                );

                MusicBand band = new MusicBand(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDate("creation_date").toLocalDate(),
                        rs.getLong("number_of_participants"),
                        rs.getString("description"),
                        coordinates,
                        rs.getLong("singles_count"),
                        rs.getString("genre") != null ? MusicGenre.valueOf(rs.getString("genre")) : null,
                        frontMan,
                        rs.getString("owner_name")
                );

                collection.add(band);
            }
            Server.logger.info("Loaded " + collection.size() + " bands from database into PriorityQueue");
            return new ExecutionStatus(true, "Collection loaded!");

        } catch (SQLException e) {
            Server.logger.severe("Error loading collection: " + e.getMessage());
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }

    public ExecutionStatus getUserBands(Pair<String, String> user) {
        Integer userId = getUserId(user.getFirst());
        if (userId == null) {
            return new ExecutionStatus(false, "User not found!");
        }

        String query = "SELECT id, name FROM music_bands WHERE user_id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getLong("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append("\n");
            }
            return new ExecutionStatus(true, sb.toString());
        } catch (SQLException e) {
            return new ExecutionStatus(false, "Database error: " + e.getMessage());
        }
    }
}
