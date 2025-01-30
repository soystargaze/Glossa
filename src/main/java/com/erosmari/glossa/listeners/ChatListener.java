package com.erosmari.glossa.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.erosmari.glossa.chat.TranslationChatRenderer;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.renderer(TranslationChatRenderer.INSTANCE);
    }
}