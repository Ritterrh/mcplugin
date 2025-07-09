package de.lobby.onevsone.lobby;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import de.lobby.onevsone.LocationUtil;

public class LobbyManager {

    private final Location lobbySpawn;

    public LobbyManager(Location lobbySpawn, Settings settings) {
        this.lobbySpawn = settings.getLobby();
    }

    public void sendToLobby(Player player) {
        player.teleport(lobbySpawn);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage("§aDu befindest dich jetzt in der Lobby.");
    }
}
