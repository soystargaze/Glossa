package com.erosmari.glossa.commands;

import com.erosmari.glossa.utils.LanguageManager;
import com.erosmari.glossa.utils.TranslationHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.entity.Player;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class LangCommand {

    // Lista de idiomas soportados (opcional, puedes usar una base de datos en el futuro)
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "es", "fr", "de", "it", "pt", "ru", "ja", "zh");

    public LangCommand() {
    }

    /**
     * Registra el subcomando /polyglot lang set <idioma>
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("lang")
                .then(
                        Commands.literal("set")
                                .then(
                                        Commands.argument("language", StringArgumentType.word())
                                                .executes(context -> {
                                                    new LangCommand().execute(context.getSource(),
                                                            context.getArgument("language", String.class));
                                                    return 1;
                                                })
                                )
                );
    }

    /**
     * Ejecuta el cambio de idioma del jugador.
     */
    public void execute(CommandSourceStack source, String idioma) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TranslationHandler.get("command.only_players"));
            return;
        }

        // Convertir el idioma a minúsculas
        idioma = idioma.toLowerCase();

        // Validar si el idioma es soportado
        if (!SUPPORTED_LANGUAGES.contains(idioma)) {
            player.sendMessage(TranslationHandler.get("command.lang.invalid"));
            return;
        }

        // Guardar el idioma del jugador
        LanguageManager.setPlayerLanguage(player, idioma);
        player.sendMessage(TranslationHandler.getFormatted("command.lang.set", idioma));
    }
}