package de.lobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

public class RelaodCommand {
    private LobbySystemMain lobbySystemMain;
    private static Settings settings;
    public RelaodCommand(LobbySystemMain lobbySystemMain, Settings settings) {
        this.lobbySystemMain = lobbySystemMain;
        this.settings = settings;
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                           
                            settings.reloadConfig();
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
