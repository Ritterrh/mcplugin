package de.lobby.commands;

import de.lobby.onevsone.DuelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelLeaveQueCommand implements CommandExecutor {

    private final DuelManager duelManager;


    public DuelLeaveQueCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        duelManager.leaveQueue(player);
        return true;
    }
}
