package de.lobby.onevsone;

import de.lobby.onevsone.lobby.LobbyManager;
import de.lobby.util.ChatUtil;
import de.lobby.util.KitManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Repräsentiert ein aktives 1vs1-Duell zwischen zwei Spielern.
 *
 * Verantwortlich für:
 * - Start & Ende eines Matches
 * - Teleportation der Spieler
 * - Kit-Zuweisung
 * - Health-Reset
 */
public class DuelMatch {

    private final Player player1;
    private final Player player2;

    private final Location spawn1;
    private final Location spawn2;

    private final DuelManager duelManager;

    public DuelMatch(Player player1, Player player2, Location spawn1, Location spawn2, DuelManager duelManager) {
        this.player1 = player1;
        this.player2 = player2;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.duelManager = duelManager;
    }

    /**
     * Startet das Match – teleportiert beide Spieler, rüstet sie aus und setzt sie auf SURVIVAL.
     */
    public void start() {
        preparePlayer(player1, spawn1, player2.getName());
        preparePlayer(player2, spawn2, player1.getName());
    }

    /**
     * Beendet das Match – sendet Nachrichten und teleportiert beide Spieler zurück in die Lobby.
     */
    public void end(Player winner, Player loser, LobbyManager lobbyManager) {
        winner.sendMessage(ChatUtil.success("Du hast gewonnen!"));
        loser.sendMessage(ChatUtil.error("Du hast verloren."));

        duelManager.sendToLobby(winner);
        duelManager.sendToLobby(loser);
    }

    /**
     * Gibt den Gegner eines bestimmten Spielers zurück.
     */
    public Player getOpponent(Player player) {
        return player.getUniqueId().equals(player1.getUniqueId()) ? player2 : player1;
    }

    private void preparePlayer(Player player, Location spawn, String opponentName) {
        player.teleport(spawn);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0);
        player.setFoodLevel(20);

        KitManager.giveDefaultKit(player);

        Component msg = ChatUtil.msg("Du kämpfst gegen ", net.kyori.adventure.text.format.NamedTextColor.GOLD)
                .append(Component.text(opponentName, net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        player.sendMessage(msg);
    }
}
