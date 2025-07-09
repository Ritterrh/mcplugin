package de.lobby.onevsone;

import de.lobby.LobbySystemMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocationUtil {

    /**
     * Speichert eine Location in der Config im Format: welt,x,y,z,yaw,pitch
     */
    public static void saveLocation(JavaPlugin plugin, String path, Location loc) {
        if (loc == null || loc.getWorld() == null) {
            plugin.getLogger().warning("§c[LOBBY] Ungültige Location beim Speichern unter '" + path + "'");
            return;
        }

        String data = String.join(",",
                loc.getWorld().getName(),
                String.valueOf(loc.getX()),
                String.valueOf(loc.getY()),
                String.valueOf(loc.getZ()),
                String.valueOf(loc.getYaw()),
                String.valueOf(loc.getPitch())
        );

        plugin.getConfig().set(path, data);
        plugin.saveConfig();
        plugin.getLogger().info("§aLocation gespeichert unter '" + path + "': " + data);
    }

    /**
     * Lädt eine Location aus der Config. Gibt null zurück bei Fehler.
     */
    public static Location loadLocation(LobbySystemMain plugin, String path) {
        FileConfiguration config = plugin.getConfig();
        String raw = config.getString(path);

        if (raw == null || raw.trim().isEmpty()) {
            plugin.getLogger().warning("§c[LOBBY] Ort für '" + path + "' nicht gefunden oder leer!");
            return null;
        }

        String[] split = raw.split(",");
        if (split.length < 6) {
            plugin.getLogger().warning("§c[LOBBY] Ungültiges Format für '" + path + "': " + raw);
            return null;
        }

        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            plugin.getLogger().warning("§c[LOBBY] Welt '" + split[0] + "' existiert nicht!");
            return null;
        }

        try {
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = Float.parseFloat(split[4]);
            float pitch = Float.parseFloat(split[5]);

            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("§c[LOBBY] Fehler beim Parsen von Koordinaten für '" + path + "': " + e.getMessage());
            return null;
        }
    }
}
