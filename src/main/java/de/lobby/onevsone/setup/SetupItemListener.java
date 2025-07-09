// de.lobby.listener.SetupItemListener.java
package de.lobby.onevsone.setup;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SetupItemListener implements Listener {

    private final JavaPlugin plugin;

    public SetupItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;
        if (!"§6Duel-Setup-Werkzeug".equals(item.getItemMeta().getDisplayName())) return;

        Location loc = p.getLocation();
        FileConfiguration config = plugin.getConfig();

        switch (e.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                config.set("duel.spawn1", serialize(loc));
                p.sendMessage("§aSpawn 1 gespeichert!");
            }
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (p.isSneaking()) {
                    config.set("duel.lobby", serialize(loc));
                    p.sendMessage("§aLobby gespeichert!");
                } else {
                    config.set("duel.spawn2", serialize(loc));
                    p.sendMessage("§aSpawn 2 gespeichert!");
                }
            }
        }
        plugin.saveConfig();
        e.setCancelled(true);
    }

    private String serialize(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }
}
