package de.lobby.onevsone;

import de.lobby.LobbySystemMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
/***
 * Die Klasse DeathListener behandelt das Ereignis, wenn ein Spieler stirbt.
 * * Sie führt die Methode handleDeath des DuelManagers aus, um den Tod des Spielers zu verarbeiten.
 * * @author Rodrigo Helwig
 * * @version 1.0
 * * @since 2025-07-09
 */
public class DeathListener implements Listener {
    private final DuelManager duelManager;

    public DeathListener(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Bukkit.getScheduler().runTaskLater(LobbySystemMain.getProvidingPlugin(LobbySystemMain.class), () -> {
            duelManager.handleDeath(dead);
        }, 1L);
    }
}
