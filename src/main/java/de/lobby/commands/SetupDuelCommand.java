// de.lobby.commands.SetupDuelCommand.java
package de.lobby.commands;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetupDuelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (!player.hasPermission("onevsone.setup")) {
            player.sendMessage("§cKeine Berechtigung.");
            return true;
        }

        ItemStack setupItem = new ItemStack(Material.BLAZE_ROD); // oder Papier etc.
        ItemMeta meta = setupItem.getItemMeta();
        meta.setDisplayName("§6Duel-Setup-Werkzeug");
        setupItem.setItemMeta(meta);

        player.getInventory().addItem(setupItem);
        player.sendMessage("§aDu hast das Setup-Werkzeug erhalten!");
        return true;
    }
}
