package de.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private String name;
    private int kills;
    private int deaths;
    private boolean inMatch;

    public GamePlayer(UUID uuid) {
        this.uuid = uuid;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.name = offlinePlayer.getName();
        this.kills = 0;
        this.deaths = 0;
        this.inMatch = false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isInMatch() {
        return inMatch;
    }

    public void setInMatch(boolean inMatch) {
        this.inMatch = inMatch;
    }

    public void sendMessage(String message) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    public void sendTitle(String titleText, String subtitleText) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.showTitle(Title.title(
                    Component.text(titleText, NamedTextColor.GOLD),
                    Component.text(subtitleText, NamedTextColor.GRAY),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
        }
    }
}
