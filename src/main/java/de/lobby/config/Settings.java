package de.lobby.config;

import de.lobby.LobbySystemMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Settings {
   private FileConfiguration config;
    private LobbySystemMain plugin;

    public Settings(LobbySystemMain plugin) {
        this.plugin = plugin;
        this.reloadConfig();
    }

    //Lädt die Config neu
   public void reloadConfig(){
       plugin.reloadConfig();
   }
   //Get den Prefix
   public String getPrefix() {
       return color(config.getString("general.prefix", "&7[Plugin] "));
   }

   public int getDefaultCountdown(){
        return config.getInt("onevsone.countdown");

   }
    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
