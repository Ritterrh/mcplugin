package de.lobby.onevsone;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import de.lobby.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Behandelt den Tod eines Spielers während eines 1vs1-Matches.
 */
public class DeathListener implements Listener {

    private final DuelManager duelManager;
    private final Settings settings;

    public DeathListener(DuelManager duelManager, Settings settings) {
        this.duelManager = duelManager;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();

        Location respawnLocation = settings.getLobby(); // z. B. Lobby oder Spawnpunkt

        // ✨ Spieler sofort wiederbeleben & teleportieren (kein Death-Screen)
        new BukkitRunnable() {
            @Override
            public void run() {
                // Sofortiger Respawn (kein Deathscreen!)
                dead.spigot().respawn();

                // Wiederherstellen
                dead.setHealth(20.0);
                dead.setFoodLevel(20);
                dead.setFireTicks(0);
                dead.setFallDistance(0);
                dead.getInventory().clear();
                dead.teleport(respawnLocation);

                // Match-Handling nach Respawn
                duelManager.handleDeath(dead);
            }
        }.runTaskLater(LobbySystemMain.getPlugin(LobbySystemMain.class), 1L); // WICHTIG: 1 Tick Delay

        // Feedback an beide Spieler
        if (killer != null && killer != dead) {
            killer.sendMessage(ChatUtil.success("Du hast " + dead.getName() + " besiegt!"));
        }

        dead.sendMessage(ChatUtil.error("Du bist gestorben."));
    }
}
