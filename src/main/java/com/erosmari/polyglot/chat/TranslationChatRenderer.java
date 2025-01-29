package com.erosmari.polyglot.chat;

import com.erosmari.polyglot.utils.DeepLTranslator;
import com.erosmari.polyglot.utils.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import io.papermc.paper.chat.ChatRenderer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public class TranslationChatRenderer implements ChatRenderer {

    public static final TranslationChatRenderer INSTANCE = new TranslationChatRenderer();

    private TranslationChatRenderer() {}

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        String viewerLang = "auto";

        if (viewer instanceof Player) {
            viewerLang = LanguageManager.getPlayerLanguage((Player) viewer);
        }

        if (viewerLang.equalsIgnoreCase("auto")) {
            return sourceDisplayName.append(Component.text(": ")).append(message);
        }

        // 🔹 Convertir el mensaje original a texto plano si es Adventure JSON
        String originalText = GsonComponentSerializer.gson().serialize(message);

        // 🔹 Traducir el mensaje
        String translatedText = DeepLTranslator.translate(originalText, viewerLang);

        // 🔹 Log de depuración
        Bukkit.getLogger().info("[Polyglot] Final Translated Message: " + translatedText);

        if (translatedText.startsWith("§c[Error]")) {
            return sourceDisplayName.append(Component.text(": ")).append(Component.text(translatedText));
        }

        try {
            // 🔹 Convertir el mensaje traducido a un componente Adventure
            Component translatedComponent = GsonComponentSerializer.gson().deserialize(translatedText);
            return sourceDisplayName.append(Component.text(": ")).append(translatedComponent);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Polyglot] Failed to deserialize translated message: " + e.getMessage());
            return sourceDisplayName.append(Component.text(": ")).append(Component.text("§c[Error] Invalid JSON response!"));
        }
    }
}