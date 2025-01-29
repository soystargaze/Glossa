package com.erosmari.polyglot.utils;

import com.erosmari.polyglot.database.DatabaseHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private static final Map<Player, String> playerLanguages = new HashMap<>();

    /**
     * Obtiene el idioma del jugador. Si no tiene uno configurado, intenta cargarlo de la base de datos.
     */
    public static String getPlayerLanguage(Player player) {
        return playerLanguages.computeIfAbsent(player, p -> DatabaseHandler.getPlayerLanguage(p.getUniqueId().toString()));
    }

    /**
     * Establece el idioma del jugador y lo guarda en la base de datos.
     */
    public static void setPlayerLanguage(Player player, String language) {
        playerLanguages.put(player, language.toLowerCase());
        DatabaseHandler.savePlayerLanguage(player.getUniqueId().toString(), language);
    }
}