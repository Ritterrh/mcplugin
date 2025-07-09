package de.lobby.commands;

import de.lobby.onevsone.DuelManager;
import de.lobby.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelLeaveQueueCommand implements CommandExecutor {

    private final DuelManager duelManager;

    public DuelLeaveQueueCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.error("Nur Spieler können diesen Befehl nutzen."));
            return true;
        }

        duelManager.leaveQueue(player);
        return true;
    }
}
