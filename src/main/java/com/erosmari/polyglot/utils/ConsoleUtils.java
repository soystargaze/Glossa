package com.erosmari.polyglot.utils;

import org.bukkit.plugin.java.JavaPlugin;

import static com.erosmari.polyglot.utils.TranslationHandler.loadedKeys;

@SuppressWarnings("ALL")
public class ConsoleUtils {

    /**
     * Muestra el arte ASCII personalizado en la consola.
     *
     * @param plugin El plugin que ejecuta el mensaje.
     */
    public static void displayAsciiArt(JavaPlugin plugin) {

        final String LOCAL_TEST_MESSAGE_KEY = "plugin.logo";
        TranslationHandler.registerTemporaryTranslation(LOCAL_TEST_MESSAGE_KEY, "\n" +
                " ███████████           ████                      ████            █████   \n" +
                "░░███░░░░░███         ░░███                     ░░███           ░░███    \n" +
                " ░███    ░███  ██████  ░███  █████ ████  ███████ ░███   ██████  ███████  \n" +
                " ░██████████  ███░░███ ░███ ░░███ ░███  ███░░███ ░███  ███░░███░░░███░   \n" +
                " ░███░░░░░░  ░███ ░███ ░███  ░███ ░███ ░███ ░███ ░███ ░███ ░███  ░███    \n" +
                " ░███        ░███ ░███ ░███  ░███ ░███ ░███ ░███ ░███ ░███ ░███  ░███ ███\n" +
                " █████       ░░██████  █████ ░░███████ ░░███████ █████░░██████   ░░█████ \n" +
                "░░░░░         ░░░░░░  ░░░░░   ░░░░░███  ░░░░░███░░░░░  ░░░░░░     ░░░░░  \n" +
                "                              ███ ░███  ███ ░███                         \n" +
                "                             ░░██████  ░░██████                          \n" +
                "                              ░░░░░░    ░░░░░░                           ");
        LoggingUtils.logTranslated(LOCAL_TEST_MESSAGE_KEY);
    }

    /**
     * Muestra el mensaje de éxito en la consola.
     *
     * @param plugin El plugin que ejecuta el mensaje.
     * */
    public static void displaySuccessMessage(JavaPlugin plugin) {

        LoggingUtils.logTranslated("plugin.separator");
        LoggingUtils.logTranslated("plugin.enabled");
        LoggingUtils.logTranslated("plugin.language_loaded", TranslationHandler.getActiveLanguage(), loadedKeys);
        LoggingUtils.logTranslated("database.initialized");
        LoggingUtils.logTranslated("command.registered");
        LoggingUtils.logTranslated("events.registered");
        LoggingUtils.logTranslated("plugin.separator");
    }
}