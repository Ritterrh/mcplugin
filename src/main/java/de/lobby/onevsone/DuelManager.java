package de.lobby.onevsone;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import de.lobby.onevsone.lobby.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Die Klasse DuelManager verwaltet die Warteschlange und die aktiven Duelle
 * zwischen Spielern.
 * * Sie ermöglicht es Spielern, sich in eine Warteschlange einzureihen, um
 * gegen andere Spieler zu kämpfen.
 * * Wenn zwei Spieler in der Warteschlange sind, wird ein Duell gestartet.
 * * @author Rodrigo Helwig
 * * @version 1.0
 * * @since 2025-07-09
 * 
 */
public class DuelManager {
    private final Map<UUID, DuelMatch> activeMatches = new HashMap<>();
    private final Queue<Player> queue = new LinkedList<>();
    private final LobbyManager lobbyManager;
    private final Location spawn1;
    private final Location spawn2;
    private final LobbySystemMain lobbySystemMain;
    private final Settings settings;
    public DuelManager(LobbyManager lobbyManager, Settings settings,Location spawn1, Location spawn2) {
        lobbySystemMain = JavaPlugin.getPlugin(LobbySystemMain.class);
        this.settings = settings;
        this.lobbyManager = lobbyManager;
        this.spawn1 = settings.getSpawn1();
        this.spawn2 = settings.getSpawn2();
    }

    public void leaveQueue(Player player) {
        queue.remove(player);
    }

    public void joinQueue(Player player) {
        if (queue.contains(player)) {
            player.sendMessage("§cDu bist bereits in der Warteschlange.");
            return;
        }

        queue.add(player);
        player.sendMessage("§aDu wurdest zur Warteschlange hinzugefügt. Warte auf einen Gegner...");

        if (queue.size() >= 2) {
            Player p1 = queue.poll();
            Player p2 = queue.poll();
            DuelMatch match = new DuelMatch(p1, p2, spawn1, spawn2, this);
            assert p1 != null;
            activeMatches.put(p1.getUniqueId(), match);
            assert p2 != null;
            activeMatches.put(p2.getUniqueId(), match);
            match.start();
        }
    }

    public void sendToLobby(Player player) {
        lobbyManager.sendToLobby(player);
    }

    public void handleDeath(Player dead) {
        DuelMatch match = activeMatches.get(dead.getUniqueId());
        if (match == null)
            return;
        dead.setGameMode(GameMode.SPECTATOR);

        Player opponent = match.getOpponent(dead);

        // Match beenden (Teleport, Message etc.)
        match.end(opponent, dead, lobbyManager);

        // Entfernen aus aktiven Matches
        activeMatches.remove(dead.getUniqueId());
        activeMatches.remove(opponent.getUniqueId());
    }

}
