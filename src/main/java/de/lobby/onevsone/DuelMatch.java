package de.lobby.onevsone;

import de.lobby.config.Settings;
import de.lobby.onevsone.lobby.LobbyManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Die Klasse DuelMatch verwaltet ein 1vs1 Duell zwischen zwei Spielern.
 * 
 * Die Klase wird verwednet um ein Duell zu starten, die Spieler zu spawnen,
 * ihre Gesundheit zu setzen und sie mit einem Kit auszustatten.
 * 
 * @author Rodrigo Helwig
 * @version 1.0
 * @since 2025-07-09
 */
// Die Klasse die Das Match handelt
// Die Soll Können:
// Spieler zum Start point Spwanen lassen
// Den Spielern max health geben
public class DuelMatch implements Listener {

    private final Player player1;
    private final Player player2;
    private final Location spawn1;
    private final Location spawn2;

    private final DuelManager duelManager;


    public DuelMatch(Player p1, Player p2, Location spawn1, Location spawn2, DuelManager duelManager) {

        this.player1 = p1;
        this.player2 = p2;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;

        this.duelManager = duelManager;
   
    }

    public void start() {
        player1.setGameMode(GameMode.ADVENTURE);
        player2.setGameMode(GameMode.ADVENTURE);
        player1.teleport(spawn1);
        player2.teleport(spawn2);

        player1.sendMessage("§6Du kämpfst gegen §e" + player2.getName());
        player2.sendMessage("§6Du kämpfst gegen §e" + player1.getName());

        player1.setHealth(20);
        player2.setHealth(20);

        // Ausrüsten
        giveKit(player1);
        giveKit(player2);

        player1.setGameMode(GameMode.SURVIVAL);
        player2.setGameMode(GameMode.SURVIVAL);

    }

    public Player getOpponent(Player player) {
        if (player.getUniqueId().equals(player1.getUniqueId()))
            return player2;
        else
            return player1;
    }



    private void giveKit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    public void end(Player winner, Player loser, LobbyManager lobbyManager) {
        winner.sendMessage("§aDu hast gewonnen!");
        loser.sendMessage("§cDu hast verloren!");

        lobbyManager.sendToLobby(winner);
        lobbyManager.sendToLobby(loser);
    }
}
