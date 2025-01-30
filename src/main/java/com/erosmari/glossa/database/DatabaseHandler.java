package com.erosmari.glossa.database;

import com.erosmari.glossa.utils.LoggingUtils;
import com.erosmari.glossa.utils.TranslationHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

public class DatabaseHandler {

    private static HikariDataSource dataSource;

    /**
     * Inicializa la conexión con SQLite.
     *
     * @param plugin El plugin principal.
     */
    public static void initialize(JavaPlugin plugin) {
        if (dataSource != null) {
            LoggingUtils.logTranslated("database.init.already_initialized");
            return;
        }

        try {
            initializeSQLite(plugin);
            createTables();
        } catch (Exception e) {
            LoggingUtils.logTranslated("database.init.error", e.getMessage());
            throw new IllegalStateException(TranslationHandler.get("database.init.failed"));
        }
    }

    /**
     * Configura y conecta con SQLite.
     *
     * @param plugin El plugin principal.
     * @throws SQLException Si ocurre un error al configurar SQLite.
     */
    private static void initializeSQLite(JavaPlugin plugin) throws SQLException {
        File dbFolder = new File(plugin.getDataFolder(), "Data");
        if (!dbFolder.exists() && !dbFolder.mkdirs()) {
            throw new SQLException(TranslationHandler.getFormatted("database.sqlite.error_directory", dbFolder.getAbsolutePath()));
        }

        String dbFilePath = new File(dbFolder, "polyglot.db").getAbsolutePath();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFilePath);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("Polyglot-SQLite");

        dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Crea las tablas necesarias.
     */
    private static void createTables() {
        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            String createPlayerLanguagesTable = """
                CREATE TABLE IF NOT EXISTS player_languages (
                    uuid TEXT PRIMARY KEY,
                    language TEXT NOT NULL
                );
                """;
            stmt.executeUpdate(createPlayerLanguagesTable);
        } catch (SQLException e) {
            LoggingUtils.logTranslated("database.tables.error", e.getMessage());
        }
    }

    /**
     * Retorna una conexión del pool.
     *
     * @return Conexión activa.
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException(TranslationHandler.get("database.connection.uninitialized"));
        }
        return dataSource.getConnection();
    }

    /**
     * Guarda el idioma de un jugador en la base de datos.
     *
     * @param uuid UUID del jugador.
     * @param language Idioma seleccionado.
     */
    public static void savePlayerLanguage(String uuid, String language) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_languages (uuid, language) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET language = ?")) {
            stmt.setString(1, uuid);
            stmt.setString(2, language);
            stmt.setString(3, language);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggingUtils.logTranslated("database.language.save_error", e.getMessage());
        }
    }

    /**
     * Recupera el idioma de un jugador desde la base de datos.
     *
     * @param uuid UUID del jugador.
     * @return Idioma del jugador o "auto" si no está registrado.
     */
    public static String getPlayerLanguage(String uuid) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT language FROM player_languages WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("language");
            }
        } catch (SQLException e) {
            LoggingUtils.logTranslated("database.language.load_error", e.getMessage());
        }
        return "auto";
    }

    /**
     * Cierra el pool de conexiones.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            LoggingUtils.logTranslated("database.close.success");
        }
    }
}