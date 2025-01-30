package com.erosmari.glossa.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class ConfigHandler {

    private static FileConfiguration config;
    private static String language;
    private static String deepLApiKey;
    private static String deepLApiUrl;

    /**
     * Configura y carga los archivos de configuración.
     *
     * @param plugin El plugin principal.
     */
    public static void setup(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Cargar valores desde config.yml
        language = config.getString("language", "en_us");
        deepLApiKey = config.getString("deepl.api_key", "");
        deepLApiUrl = config.getString("deepl.api_url", "https://api-free.deepl.com/v2/translate");
    }

    /**
     * Retorna la configuración general (config.yml).
     *
     * @return Configuración general.
     */
    @SuppressWarnings("unused")
    public static FileConfiguration getConfig() {
        return config;
    }

    /**
     * Retorna el idioma configurado en config.yml.
     *
     * @return Idioma configurado.
     */
    public static String getLanguage() {
        return language;
    }

    /**
     * Obtiene la clave de la API de DeepL.
     *
     * @return Clave de la API de DeepL.
     */
    public static String getDeepLApiKey() {
        return deepLApiKey;
    }

    /**
     * Obtiene la URL de la API de DeepL.
     *
     * @return URL de la API de DeepL.
     */
    public static String getDeepLApiUrl() {
        return deepLApiUrl;
    }

    /**
     * Obtiene un valor entero de la configuración.
     *
     * @param path La ruta de la clave en el archivo de configuración.
     * @param def  El valor predeterminado si no existe la clave.
     * @return El valor entero obtenido de la configuración.
     */
    public static int getInt(String path, int def) {
        return config.getInt(path, def);
    }
}