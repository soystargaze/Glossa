package com.erosmari.polyglot.listeners;

import com.erosmari.polyglot.utils.DeepLTranslator;
import com.erosmari.polyglot.utils.LanguageManager;
import com.erosmari.polyglot.utils.LoggingUtils;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {

    public ChatListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player sender = event.getPlayer();
        String originalMessage = GsonComponentSerializer.gson().serialize(event.message());

        for (Player recipient : Bukkit.getOnlinePlayers()) {
            String recipientLang = LanguageManager.getPlayerLanguage(recipient);

            String translatedMessage = DeepLTranslator.translate(originalMessage, recipientLang);

            if (translatedMessage.startsWith("§c[Error]")) {
                recipient.sendMessage("[Error] No se pudo traducir el mensaje.");
                continue;
            }

            LoggingUtils.sendAndLog(recipient,"translate.test", sender.getName(), translatedMessage);
            recipient.sendMessage("[Polyglot] " + sender.getName() + ": " + translatedMessage);
        }
    }
}