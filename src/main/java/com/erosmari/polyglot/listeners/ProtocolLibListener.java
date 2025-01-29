package com.erosmari.polyglot.listeners;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.erosmari.polyglot.utils.DeepLTranslator;
import com.erosmari.polyglot.utils.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
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

        // Intercept chat messages (normal chat)
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player recipient = event.getPlayer();
                String recipientLang = LanguageManager.getPlayerLanguage(recipient);

                if (event.getPacket().getStrings().size() > 0) { // ✅ FIXED
                    String originalMessage = event.getPacket().getStrings().read(0);

                    if (!recipientLang.equalsIgnoreCase("auto")) {
                        event.setCancelled(true);
                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                            String translatedMessage = translationCache.computeIfAbsent(originalMessage,
                                    msg -> DeepLTranslator.translate(originalMessage, recipientLang));

                            Component translatedComponent = Component.text(translatedMessage)
                                    .hoverEvent(HoverEvent.showText(Component.text("Original: " + originalMessage)));

                            recipient.sendMessage(translatedComponent);
                        });
                    }
                }
            }
        });

        // Intercept action bar messages
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_ACTION_BAR_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player recipient = event.getPlayer();
                String recipientLang = LanguageManager.getPlayerLanguage(recipient);

                try {
                    if (event.getPacket().getStrings().size() > 0) { // ✅ FIXED
                        String originalMessage = event.getPacket().getStrings().read(0);

                        if (!recipientLang.equalsIgnoreCase("auto")) {
                            String translatedMessage = translationCache.computeIfAbsent(originalMessage,
                                    msg -> DeepLTranslator.translate(originalMessage, recipientLang));
                            event.getPacket().getStrings().write(0, translatedMessage);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error translating action bar message: " + e.getMessage());
                }
            }
        });

        // Intercept titles
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_TITLE_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player recipient = event.getPlayer();
                String recipientLang = LanguageManager.getPlayerLanguage(recipient);

                try {
                    if (event.getPacket().getStrings().size() > 0) { // ✅ FIXED
                        String originalTitle = event.getPacket().getStrings().read(0);

                        if (!recipientLang.equalsIgnoreCase("auto")) {
                            String translatedTitle = translationCache.computeIfAbsent(originalTitle,
                                    msg -> DeepLTranslator.translate(originalTitle, recipientLang));
                            event.getPacket().getStrings().write(0, translatedTitle);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error translating title: " + e.getMessage());
                }
            }
        });

        // Intercept subtitles
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_SUBTITLE_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player recipient = event.getPlayer();
                String recipientLang = LanguageManager.getPlayerLanguage(recipient);

                try {
                    if (event.getPacket().getStrings().size() > 0) { // ✅ FIXED
                        String originalSubtitle = event.getPacket().getStrings().read(0);

                        if (!recipientLang.equalsIgnoreCase("auto")) {
                            String translatedSubtitle = translationCache.computeIfAbsent(originalSubtitle,
                                    msg -> DeepLTranslator.translate(originalSubtitle, recipientLang));
                            event.getPacket().getStrings().write(0, translatedSubtitle);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error translating subtitle: " + e.getMessage());
                }
            }
        });

        // Intercept system chat messages (messages from plugins and server)
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SYSTEM_CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player recipient = event.getPlayer();
                String recipientLang = LanguageManager.getPlayerLanguage(recipient);

                try {
                    if (event.getPacket().getStrings().size() > 0) {
                        String originalMessage = event.getPacket().getStrings().read(0);

                        if (!recipientLang.equalsIgnoreCase("auto")) {
                            String translatedMessage = translationCache.computeIfAbsent(originalMessage,
                                    msg -> DeepLTranslator.translate(originalMessage, recipientLang));
                            event.getPacket().getStrings().write(0, translatedMessage);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error translating system chat message: " + e.getMessage());
                }
            }
        });
    }
}