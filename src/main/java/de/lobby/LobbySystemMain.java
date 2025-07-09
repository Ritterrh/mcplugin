package de.lobby;
import de.lobby.commands.DuelCommand;
import de.lobby.commands.DuelLeaveQueCommand;
import de.lobby.commands.SetupDuelCommand;
import de.lobby.config.Settings;
import de.lobby.onevsone.DeathListener;
import de.lobby.onevsone.DuelManager;
import de.lobby.onevsone.lobby.LobbyManager;
import de.lobby.onevsone.setup.SetupItemListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbySystemMain extends JavaPlugin implements Listener {
    private Settings settings;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = new Settings(this);
        Location lobbySpawn = new Location(Bukkit.getWorld("world"), 0, 64, 0);
        Location spawn1 = new Location(Bukkit.getWorld("world"), 100, 64, 100);
        Location spawn2 = new Location(Bukkit.getWorld("world"), 110, 64, 100);

        LobbyManager lobbyManager = new LobbyManager(lobbySpawn, settings);
        DuelManager duelManager = new DuelManager(lobbyManager,settings, spawn1, spawn2);
        getCommand("setupduel").setExecutor(new SetupDuelCommand());
        getCommand("duel").setExecutor(new DuelCommand(duelManager));
        getCommand("leave").setExecutor(new DuelLeaveQueCommand(duelManager));
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new SetupItemListener(this, settings), this);
        Bukkit.getPluginManager().registerEvents( new DeathListener(duelManager), this);

    }

    @Override
    public void onLoad() {
        super.onLoad();

    }

    public Settings getSettings() {
        return settings;
    }
}