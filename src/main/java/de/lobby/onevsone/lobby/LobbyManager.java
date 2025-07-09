package de.lobby.onevsone.lobby;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LobbyManager {

    private final Location lobbySpawn;

    public LobbyManager(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public void sendToLobby(Player player) {
        player.teleport(lobbySpawn);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage("§aDu befindest dich jetzt in der Lobby.");
    }
}
