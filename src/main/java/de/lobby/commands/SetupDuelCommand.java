package de.lobby.commands;

import de.lobby.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Gibt dem Spieler das Duel-Setup-Werkzeug.
 */
public class SetupDuelCommand implements CommandExecutor {

    private static final String PERMISSION = "onevsone.setup";
    private static final NamespacedKey SETUP_KEY = new NamespacedKey("duel", "setup_tool");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(ChatUtil.error("Du hast keine Berechtigung, diesen Befehl zu nutzen."));
            return true;
        }

        ItemStack setupTool = createSetupTool();
        player.getInventory().addItem(setupTool);

        player.sendMessage(ChatUtil.success("Du hast das Setup-Werkzeug erhalten."));
        return true;
    }

    private ItemStack createSetupTool() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Duel-Setup-Werkzeug", NamedTextColor.GOLD));
        meta.lore(java.util.List.of(
                Component.text("Rechtsklick, um das Setup-Menü zu öffnen.", NamedTextColor.GRAY)
        ));

        // Eindeutige Markierung für den Listener
        meta.getPersistentDataContainer().set(SETUP_KEY, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }
}
