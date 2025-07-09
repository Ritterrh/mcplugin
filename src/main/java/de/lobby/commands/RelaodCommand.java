package de.lobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.lobby.LobbySystemMain;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RelaodCommand {
    private LobbySystemMain lobbySystemMain;

    public RelaodCommand(LobbySystemMain lobbySystemMain) {
        this.lobbySystemMain = lobbySystemMain;
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            final Player targetPlayer = playerSelector.resolve(ctx.getSource()).getFirst();
                            final CommandSender sender = ctx.getSource().getSender();

                            targetPlayer.sendPlainMessage(sender.getName() + " started partying with you!");
                            sender.sendPlainMessage("You are now partying with " + targetPlayer.getName() + "!");

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
