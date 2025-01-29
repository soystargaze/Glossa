package com.erosmari.polyglot.chat;

import com.erosmari.polyglot.utils.DeepLTranslator;
import com.erosmari.polyglot.utils.LanguageManager;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TranslationChatRenderer implements ChatRenderer {

    public static final TranslationChatRenderer INSTANCE = new TranslationChatRenderer();

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        if (!(viewer instanceof Player recipient)) {
            return sourceDisplayName.append(Component.text(": ")).append(message);
        }

        String recipientLang = LanguageManager.getPlayerLanguage(recipient);
        String originalMessage = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(message);

        if (recipientLang.equalsIgnoreCase("auto")) {
            return sourceDisplayName.append(Component.text(": ")).append(message);
        }

        // 🔹 Traducir el mensaje usando DeepL
        String translatedMessage = DeepLTranslator.translate(originalMessage, recipientLang);

        return sourceDisplayName
                .append(Component.text(": "))
                .append(Component.text(translatedMessage));
    }
}