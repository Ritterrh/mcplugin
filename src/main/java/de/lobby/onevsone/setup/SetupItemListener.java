// de.lobby.listener.SetupItemListener.java
package de.lobby.onevsone.setup;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SetupItemListener implements Listener {

    private final LobbySystemMain plugin;
    private final Settings settings;

    public SetupItemListener(LobbySystemMain plugin, Settings settings) {
        this.settings = settings;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta())
            return;
        if (!"§6Duel-Setup-Werkzeug".equals(item.getItemMeta().getDisplayName()))
            return;

        Location loc = p.getLocation();
        if (loc.getWorld() == null) {
            p.sendMessage("§cUngültige Welt! Bitte in einer gültigen Welt sein.");
            return;
        }
        switch (e.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                settings.setSpwan1(loc);
                p.sendMessage("§aSpawn 1 gespeichert!");
            }
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (p.isSneaking()) {
                    settings.setLobby(loc);
                    p.sendMessage("§aLobby gespeichert!");
                } else {
                    settings.setSpawn2(loc);
                    p.sendMessage("§aSpawn 2 gespeichert!");
                }
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + e.getAction());
        }
        plugin.saveConfig();
        e.setCancelled(true);
    }

 
}
