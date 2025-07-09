package de.lobby.config;

import de.lobby.LobbySystemMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
    private FileConfiguration config;
    private static LobbySystemMain plugin;

    public Settings(LobbySystemMain plugin) {
        Settings.plugin = plugin;

        this.config = plugin.getConfig();
        this.reloadConfig();
    }

    // Lädt die Config neu
    public void reloadConfig() {
        plugin.reloadConfig();
    }

    // Get den Prefix
    public String getPrefix() {
        return color(config.getString("general.prefix", "&7[Plugin] "));
    }

    public int getDefaultCountdown() {
        return config.getInt("onevsone.countdown");

    }

    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public void setSpwan1(Location spawn1) {
        plugin.getConfig().set("duel.spawn1.world", spawn1.getWorld().getName());
        plugin.getConfig().set("duel.spawn1.x", spawn1.getX());
        plugin.getConfig().set("duel.spawn1.y", spawn1.getY());
        plugin.getConfig().set("duel.spawn1.z", spawn1.getZ());
        plugin.getConfig().set("duel.spawn1.yaw", spawn1.getYaw());
        plugin.getConfig().set("duel.spawn1.pitch", spawn1.getPitch());
        plugin.saveConfig();
    }

    public void setSpawn2(Location spawn2) {
        plugin.getConfig().set("duel.spawn2.world", spawn2.getWorld().getName());
        plugin.getConfig().set("duel.spawn2.x", spawn2.getX());
        plugin.getConfig().set("duel.spawn2.y", spawn2.getY());
        plugin.getConfig().set("duel.spawn2.z", spawn2.getZ());
        plugin.getConfig().set("duel.spawn2.yaw", spawn2.getYaw());
        plugin.getConfig().set("duel.spawn2.pitch", spawn2.getPitch());
        plugin.saveConfig();
    }

    public void setLobby(Location lobby) {
        plugin.getConfig().set("duel.lobby.world", lobby.getWorld().getName());
        plugin.getConfig().set("duel.lobby.x", lobby.getX());
        plugin.getConfig().set("duel.lobby.y", lobby.getY());
        plugin.getConfig().set("duel.lobby.z", lobby.getZ());
        plugin.getConfig().set("duel.lobby.yaw", lobby.getYaw());
        plugin.getConfig().set("duel.lobby.pitch", lobby.getPitch());
        plugin.saveConfig();
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

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    private Location getLocation(String path) {
        if (!plugin.getConfig().contains(path + ".world"))
            return null;
        World world = Bukkit.getWorld(plugin.getConfig().getString(path + ".world"));
        double x = plugin.getConfig().getDouble(path + ".x");
        double y = plugin.getConfig().getDouble(path + ".y");
        double z = plugin.getConfig().getDouble(path + ".z");
        float yaw = (float) plugin.getConfig().getDouble(path + ".yaw");
        float pitch = (float) plugin.getConfig().getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
