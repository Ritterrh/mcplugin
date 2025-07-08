package de.lobby;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Lobby extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {


        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
       getServer().broadcast(Component.text("[Lobby] " + event.getPlayer().getName()));
    }
}