package com.erosmari.polyglot;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.erosmari.polyglot.commands.PolyglotCommandManager;
import com.erosmari.polyglot.config.ConfigHandler;
import com.erosmari.polyglot.database.DatabaseHandler;
import com.erosmari.polyglot.listeners.ChatListener;
import com.erosmari.polyglot.listeners.ProtocolLibListener;
import com.erosmari.polyglot.utils.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Polyglot extends JavaPlugin implements Listener {

    private static Polyglot instance;
    private PolyglotCommandManager commandManager;
    private ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        instance = this;
        initializePlugin();
    }

    @Override
    public void onDisable() {
        AsyncExecutor.shutdown();
        DatabaseHandler.close();
        LoggingUtils.logTranslated("plugin.disabled");
        instance = null;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initializePlugin() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        loadConfigurations();

        ConsoleUtils.displayAsciiArt(this);
        LoggingUtils.logTranslated("plugin.separator");
        LoggingUtils.logTranslated("plugin.name");
        LoggingUtils.logTranslated("plugin.version", getPluginMeta().getVersion());
        LoggingUtils.logTranslated("plugin.author", getPluginMeta().getAuthors().getFirst());
        LoggingUtils.logTranslated("plugin.separator");

        try {
            initializeDatabase();
            initializeSystems();
            registerEvents();

            ConsoleUtils.displaySuccessMessage(this);
        } catch (Exception e) {
            LoggingUtils.logTranslated("plugin.enable_error", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static Polyglot getInstance() {
        return instance;
    }

    private void loadConfigurations() {
        ConfigHandler.setup(this);
        AsyncExecutor.initialize();
        setupTranslations();
        TranslationHandler.loadTranslations(this, ConfigHandler.getLanguage());
    }

    private void setupTranslations() {
        File translationsFolder = new File(getDataFolder(), "Translations");

        if (!translationsFolder.exists() && !translationsFolder.mkdirs()) {
            LoggingUtils.logTranslated("translations.folder_error");
            return;
        }

        String[] defaultLanguages = {"en_us.yml"};

        for (String languageFile : defaultLanguages) {
            saveDefaultTranslation(languageFile);
        }

        File[] translationFiles = translationsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (translationFiles != null) {
            for (File file : translationFiles) {
                String language = file.getName().replace(".yml", "");
                TranslationHandler.loadTranslations(this, language);
            }
        }

        String configuredLanguage = ConfigHandler.getLanguage();
        if (TranslationHandler.isLanguageAvailable(configuredLanguage)) {
            TranslationHandler.setActiveLanguage(configuredLanguage);
        } else {
            final String LICENSE_SUCCESS_KEY = "translations.language_not_found";
            TranslationHandler.registerTemporaryTranslation(LICENSE_SUCCESS_KEY, "Language not found: {0}");
            LoggingUtils.logTranslated(LICENSE_SUCCESS_KEY, configuredLanguage);
        }
    }

    private void saveDefaultTranslation(String fileName) {
        File translationFile = new File(getDataFolder(), "Translations/" + fileName);

        if (!translationFile.exists()) {
            try {
                saveResource("Translations/" + fileName, false);
            } catch (Exception ignored) {
            }
        }
    }

    private void initializeDatabase() {
        try {
            DatabaseHandler.initialize(this);
        } catch (Exception e) {
            LoggingUtils.logTranslated("database.init_error", e.getMessage());
            throw new IllegalStateException(TranslationHandler.get("database.init_fatal_error"));
        }
    }

    private void initializeSystems() {
        initializeCommandManager();
    }

    private void initializeCommandManager() {
        try {
            if (commandManager == null) {
                commandManager = new PolyglotCommandManager(this);
                commandManager.registerCommands();
            }
        } catch (Exception e) {
            LoggingUtils.logTranslated("command.register_error", e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerEvents() {
        try {
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
            new ProtocolLibListener(this, protocolManager).registerPacketListeners();
        } catch (Exception e) {
            LoggingUtils.logTranslated("events.register_error", e.getMessage());
        }
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

}