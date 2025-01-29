package com.erosmari.polyglot.commands;

import com.erosmari.polyglot.Polyglot;
import com.erosmari.polyglot.utils.TranslationHandler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class PolyglotCommandManager {

    private final Polyglot plugin;

    public PolyglotCommandManager(Polyglot plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra todos los comandos principales y sus subcomandos.
     */
    public void registerCommands() {
        LifecycleEventManager<@org.jetbrains.annotations.NotNull Plugin> manager = plugin.getLifecycleManager();
        // Registrar comandos usando el evento COMMANDS
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();

            commands.register(
                    Commands.literal("polyglot")
                            .requires(source -> source.getSender().hasPermission("polyglot.use"))
                            .executes(ctx -> {
                                CommandSourceStack source = ctx.getSource();
                                source.getSender().sendMessage(TranslationHandler.getPlayerMessage("command.usage"));
                                return 1;
                            })
                            .then(ReloadCommand.register(plugin))
                            .build()
            );
        });
    }
}