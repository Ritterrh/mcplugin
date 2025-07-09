package de.lobby.onevsone.lobby;

import de.lobby.config.Settings;
import de.lobby.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Verwaltet das Zurücksetzen und Teleportieren von Spielern in die Lobby.
 */
public class LobbyManager {

    private Settings settings;

    public LobbyManager(Settings settings) {
        this.settings = settings;
    }

    /**
     * Teleportiert den Spieler in die Lobby und setzt seinen Zustand zurück.
     *
     * @param player Der Spieler, der in die Lobby teleportiert werden soll.
     */
    public void sendToLobby(Player player) {
        Location lobbySpawn = settings.getLobby();
        if (lobbySpawn == null) {
            player.sendMessage(ChatUtil.error("Lobby-Spawn ist nicht gesetzt."));
            return;
        }

        player.teleport(lobbySpawn);
        resetPlayerState(player);

        Component msg = ChatUtil.success("Du befindest dich jetzt in der Lobby.");
        player.sendMessage(msg);
    }

    /**
     * Setzt den Zustand des Spielers zurück (Inventar, Gesundheit, Hunger, etc.).
     *
     * @param player Der Spieler, dessen Zustand zurückgesetzt werden soll.
     */
    private void resetPlayerState(Player player) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);
    }
}
