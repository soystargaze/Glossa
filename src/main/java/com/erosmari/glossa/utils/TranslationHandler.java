package com.erosmari.glossa.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranslationHandler {

    private static final Map<String, String> translations = new HashMap<>();
    public static int loadedKeys = 0;
    private static String activeLanguage = "en_us";

    public static void loadTranslations(JavaPlugin plugin, String language) {
        File translationsFolder = new File(plugin.getDataFolder(), "Translations");
        if (!translationsFolder.exists() && !translationsFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create the translations folder.");
            return;
        }

        File langFile = new File(translationsFolder, language + ".yml");

        if (!langFile.exists()) {
            createDefaultTranslationFile(plugin, langFile, language);
        }

        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        loadedKeys = 0;
        translations.clear(); // Limpiar traducciones previas antes de cargar nuevas

        for (String key : langConfig.getKeys(true)) {
            if (langConfig.isString(key)) {
                translations.put(key, langConfig.getString(key));
                loadedKeys++;
            }
        }

        activeLanguage = language;
    }

    private static void createDefaultTranslationFile(JavaPlugin plugin, File langFile, String language) {
        try {
            if (langFile.createNewFile()) {
                String resourcePath = "Translations/" + language + ".yml";
                if (plugin.getResource(resourcePath) != null) {
                    plugin.saveResource(resourcePath, false);
                    plugin.getLogger().info("Default translation file '" + language + ".yml' created.");
                } else {
                    plugin.getLogger().warning("Default resource not found for '" + language + ".yml'.");
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create the translation file: " + langFile.getName());
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Error creating the translation file", e);
        }
    }

    public static String get(String key) {
        return translations.getOrDefault(key, "Translation not found: " + key + "!");
    }

    public static String getFormatted(String key, Object... args) {
        String template = translations.getOrDefault(key, "Translation not found: " + key + "!");
        for (int i = 0; i < args.length; i++) {
            template = template.replace("{" + i + "}", args[i].toString());
        }
        return template;
    }

    public static void clearTranslations() {
        translations.clear();
    }

    public static int getLoadedTranslationsCount() {
        return translations.size();
    }

    public static Component getPlayerMessage(String key, Object... args) {
        String prefix = translations.getOrDefault("plugin.prefix", "<color:#d4d4d4>[</color><gradient:#6EBFFF:#6600FF>Polyglot</gradient><color:#d4d4d4>]</color> ");
        String dynamicColor = translations.getOrDefault("plugin.dynamic_color", "<color:#6EBFFF>");
        String template = translations.getOrDefault(key, "Translation not found: " + key + "!");

        for (int i = 0; i < args.length; i++) {
            String coloredArg = dynamicColor + args[i].toString() + "</color>";
            template = template.replace("{" + i + "}", coloredArg);
        }

        String fullMessage = prefix + template;

        if (fullMessage.contains("&")) {
            fullMessage = fullMessage.replace("&", "§");
        }

        if (fullMessage.contains("§")) {
            return LegacyComponentSerializer.legacySection().deserialize(fullMessage);
        }

        return MiniMessage.miniMessage().deserialize(fullMessage);
    }

    public static Component getLogMessage(String key, Object... args) {
        String prefix = translations.getOrDefault("plugin.prefix", "<gray>[<gradient:#6EBFFF:#6600FF>Polyglot</gradient>]</gray> ");
        String template = translations.getOrDefault(key, "Translation not found: " + key + "!");
        String dynamicColor = translations.getOrDefault("plugin.dynamic_color", "<color:#6EBFFF>");

        for (int i = 0; i < args.length; i++) {
            String coloredArg = dynamicColor + args[i].toString() + "</color>";
            template = template.replace("{" + i + "}", coloredArg);
        }

        String fullMessage = prefix + template;

        if (fullMessage.contains("&")) {
            fullMessage = fullMessage.replace("&", "§");
        }

        if (fullMessage.contains("§")) {
            return LegacyComponentSerializer.legacySection().deserialize(fullMessage);
        }

        return MiniMessage.miniMessage().deserialize(fullMessage);
    }

    public static void registerTemporaryTranslation(String key, String message) {
        if (!translations.containsKey(key)) {
            translations.put(key, message);
        }
    }

    public static boolean isLanguageAvailable(String language) {
        File langFile = new File(JavaPlugin.getProvidingPlugin(TranslationHandler.class).getDataFolder(), "Translations/" + language + ".yml");
        return langFile.exists();
    }
    public static void setActiveLanguage(String language) {
        if (isLanguageAvailable(language)) {
            activeLanguage = language;
        }
    }
    public static String getActiveLanguage() {
        return activeLanguage;
    }
}