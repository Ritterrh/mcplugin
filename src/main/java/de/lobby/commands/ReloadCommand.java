package de.lobby.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.lobby.config.Settings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Settings settings) {
        dispatcher.register(
                Commands.literal("reloadsettings")
                        .requires(source -> source.getSender().hasPermission("onevsone.admin"))
                        .executes(ctx -> {
                            settings.reloadConfig();

                            ctx.getSource().getSender().sendMessage(Component.text("Die Einstellungen wurden neu geladen.", NamedTextColor.GREEN));
                            return 1;
                        })
        );
    }
}
