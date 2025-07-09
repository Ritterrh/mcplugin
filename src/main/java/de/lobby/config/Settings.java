package de.lobby.config;

import de.lobby.LobbySystemMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class Settings {

    private final LobbySystemMain plugin;
    private FileConfiguration config;

    public Settings(LobbySystemMain plugin) {
        this.plugin = plugin;
        this.reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public Component getPrefix() {
        String prefix = config.getString("general.prefix", "&7[1vs1] ");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
    }

    public int getDefaultCountdown() {
        return config.getInt("onevsone.countdown", 5);
    }

    public Location getSpawn1() {
        return getLocation("duel.spawn1");
    }

    public Location getSpawn2() {
        return getLocation("duel.spawn2");
    }

    public Location getLobby() {
        return getLocation("duel.lobby");
    }

    public void setSpawn1(Location location) {
        setLocation("duel.spawn1", location);
    }

    public void setSpawn2(Location location) {
        setLocation("duel.spawn2", location);
    }

    public void setLobby(Location location) {
        setLocation("duel.lobby", location);
    }

    private void setLocation(String path, Location loc) {
        World world = Objects.requireNonNull(loc.getWorld(), "Location world cannot be null");
        config.set(path + ".world", world.getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }

    
    private Location getLocation(String path) {
        if (!config.contains(path + ".world")) return null;

        World world = Bukkit.getWorld(config.getString(path + ".world"));
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}
