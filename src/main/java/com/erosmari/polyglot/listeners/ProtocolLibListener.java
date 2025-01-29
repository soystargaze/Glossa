package com.erosmari.polyglot.listeners;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.erosmari.polyglot.utils.DeepLTranslator;
import com.erosmari.polyglot.utils.LanguageManager;
import com.erosmari.polyglot.utils.LoggingUtils;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolLibListener {

    private final ProtocolManager protocolManager;
    private final Plugin plugin;
    private final Map<String, String> translationCache = new ConcurrentHashMap<>();

    public ProtocolLibListener(Plugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;
    }

    public void registerPacketListeners() {
        // Interceptar chat de plugins
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                translatePacket(event, "translate.error.chat");
            }
        });

        // Interceptar Action Bar
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_ACTION_BAR_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                translatePacket(event, "translate.error.action_bar");
            }
        });

        // Interceptar Títulos
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_TITLE_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                translatePacket(event, "translate.error.title");
            }
        });

        // Interceptar Subtítulos
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_SUBTITLE_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                translatePacket(event, "translate.error.subtitle");
            }
        });
    }

    private void translatePacket(PacketEvent event, String errorKey) {
        Player recipient = event.getPlayer();
        String recipientLang = LanguageManager.getPlayerLanguage(recipient);

        try {
            if (event.getPacket().getChatComponents().size() > 0) {
                String originalMessage = event.getPacket().getChatComponents().read(0).getJson();

                if (!recipientLang.equalsIgnoreCase("auto")) {
                    String translatedMessage = translationCache.computeIfAbsent(originalMessage,
                            msg -> DeepLTranslator.translate(originalMessage, recipientLang));

                    event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(translatedMessage));
                }
            }
        } catch (Exception e) {
            LoggingUtils.logTranslated(errorKey, e.getMessage());
        }
    }
}