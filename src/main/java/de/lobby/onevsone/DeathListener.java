package de.lobby.onevsone;

import de.lobby.LobbySystemMain;
import de.lobby.util.ChatUtil;
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

    public DeathListener(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();

        new BukkitRunnable() {
            @Override
            public void run() {
                duelManager.handleDeath(dead);
            }
        }.runTask(LobbySystemMain.getPlugin(LobbySystemMain.class));

        if (killer != null && killer != dead) {
            killer.sendMessage(ChatUtil.success("Du hast " + dead.getName() + " besiegt!"));
        }

        dead.sendMessage(ChatUtil.error("Du bist gestorben."));
    }
}
