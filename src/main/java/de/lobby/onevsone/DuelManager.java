package de.lobby.onevsone;

import de.lobby.config.Settings;
import de.lobby.onevsone.lobby.LobbyManager;
import de.lobby.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DuelManager {

    private final Map<UUID, DuelMatch> activeMatches = new HashMap<>();
    private final Queue<Player> queue = new ConcurrentLinkedQueue<>();

    private final LobbyManager lobbyManager;
    private final Settings settings;

    public DuelManager(LobbyManager lobbyManager, Settings settings) {
        this.lobbyManager = lobbyManager;
        this.settings = settings;
    }

    public void joinQueue(Player player) {
        if (queue.contains(player)) {
            player.sendMessage(ChatUtil.error("Du bist bereits in der Warteschlange."));
            return;
        }

        queue.add(player);
        player.sendMessage(ChatUtil.success("Du wurdest zur Warteschlange hinzugefügt. Warte auf einen Gegner..."));

        tryStartMatch();
    }

    public void leaveQueue(Player player) {
        if (queue.remove(player)) {
            player.sendMessage(ChatUtil.success("Du hast die Warteschlange verlassen."));
        } else {
            player.sendMessage(ChatUtil.error("Du befindest dich nicht in der Warteschlange."));
        }
    }

    private void tryStartMatch() {
        if (queue.size() < 2) return;

        Player p1 = queue.poll();
        Player p2 = queue.poll();

        if (p1 == null || p2 == null) return;

        Location spawn1 = settings.getSpawn1();
        Location spawn2 = settings.getSpawn2();

        if (spawn1 == null || spawn2 == null) {
            Component msg = ChatUtil.error("Spawns wurden nicht gesetzt. Bitte Setup abschließen!");
            if (p1 != null) p1.sendMessage(msg);
            if (p2 != null) p2.sendMessage(msg);
            return;
        }

        DuelMatch match = new DuelMatch(p1, p2, spawn1, spawn2, this);

        activeMatches.put(p1.getUniqueId(), match);
        activeMatches.put(p2.getUniqueId(), match);

        match.start();
    }

    public void handleDeath(Player dead) {
        DuelMatch match = activeMatches.get(dead.getUniqueId());
        if (match == null) return;

        Player opponent = match.getOpponent(dead);

        match.end(opponent, dead, lobbyManager);

        activeMatches.remove(dead.getUniqueId());
        activeMatches.remove(opponent.getUniqueId());
    }

    public void sendToLobby(Player player) {
        lobbyManager.sendToLobby(player);
    }
}
