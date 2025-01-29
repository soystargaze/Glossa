package com.erosmari.polyglot.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.erosmari.polyglot.chat.TranslationChatRenderer;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.renderer(TranslationChatRenderer.INSTANCE); // Usa el Singleton Renderer
    }
}