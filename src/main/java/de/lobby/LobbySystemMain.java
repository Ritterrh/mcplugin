package de.lobby;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.lobby.commands.DuelCommand;
import de.lobby.commands.DuelLeaveQueueCommand;
import de.lobby.commands.ReloadSettingsCommand;
import de.lobby.commands.SetupDuelCommand;
import de.lobby.config.Settings;
import de.lobby.onevsone.DeathListener;
import de.lobby.onevsone.DuelManager;
import de.lobby.onevsone.lobby.LobbyManager;
import de.lobby.onevsone.setup.SetupItemListener;

public final class LobbySystemMain extends JavaPlugin {

    private Settings settings;
    private DuelManager duelManager;
    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initSettings();
        initManagers();
        registerCommands();
        registerEvents();
        getLogger().info("LobbySystem erfolgreich gestartet.");
    }

    private void initSettings() {
        this.settings = new Settings(this);
    }

    private void initManagers() {
        this.lobbyManager = new LobbyManager(settings);
        this.duelManager = new DuelManager(lobbyManager, settings);
    }

    private void registerCommands() {
        getCommand("duel").setExecutor(new DuelCommand(duelManager));
        getCommand("leave").setExecutor(new DuelLeaveQueueCommand(duelManager));
        getCommand("setupduel").setExecutor(new SetupDuelCommand());
        getCommand("reloadsettings").setExecutor(new ReloadSettingsCommand(settings));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new SetupItemListener(this, settings), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(duelManager, settings), this);
    }

    public Settings getSettings() {
        return settings;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
}
