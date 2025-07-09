package de.lobby.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Stellt Kits für Spieler im 1vs1 bereit.
 */
public class KitManager {

    public static void giveDefaultKit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player.setHealth(20.0);
        player.setFoodLevel(20);
    }


}
