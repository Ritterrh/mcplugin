package de.lobby.commands;

import de.lobby.config.Settings;
import de.lobby.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadSettingsCommand implements CommandExecutor {

    private final Settings settings;

    public ReloadSettingsCommand(Settings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("onevsone.admin")) {
            sender.sendMessage(ChatUtil.error("Keine Berechtigung."));
            return true;
        }

        settings.reloadConfig();
        sender.sendMessage(ChatUtil.success("Konfiguration erfolgreich neu geladen."));
        return true;
    }
}
