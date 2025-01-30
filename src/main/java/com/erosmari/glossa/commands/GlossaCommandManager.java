package com.erosmari.glossa.commands;

import com.erosmari.glossa.Glossa;
import com.erosmari.glossa.utils.TranslationHandler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class GlossaCommandManager {

    private final Glossa plugin;

    public GlossaCommandManager(Glossa plugin) {
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
                    Commands.literal("glossa")
                            .requires(source -> source.getSender().hasPermission("glossa.use"))
                            .executes(ctx -> {
                                CommandSourceStack source = ctx.getSource();
                                source.getSender().sendMessage(TranslationHandler.getPlayerMessage("command.usage"));
                                return 1;
                            })
                            .then(ReloadCommand.register(plugin))
                            .then(LangCommand.register())
                            .build()
            );
        });
    }
}