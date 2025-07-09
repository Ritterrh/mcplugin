package de.lobby.onevsone.setup;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SetupItemListener implements Listener {

    private final LobbySystemMain plugin;
    private final Settings settings;
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final Set<UUID> shownAnimation = new HashSet<>();

    public SetupItemListener(LobbySystemMain plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta())
            return;
        if (!"§6Duel-Setup-Werkzeug".equals(item.getItemMeta().getDisplayName()))
            return;

        openSetupMenu(p);
        e.setCancelled(true);
    }

    private void openSetupMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Duel Setup");

        // Fortschrittsanzeige (Slots 0–8)
        for (int i = 0; i < 9; i++) {
            ItemStack progressItem;

            if ((i == 2 && settings.getSpawn1() != null) ||
                    (i == 4 && settings.getSpawn2() != null) ||
                    (i == 6 && settings.getLobby() != null)) {
                progressItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            } else {
                progressItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            }

            ItemMeta meta = progressItem.getItemMeta();
            meta.setDisplayName("§7Setup-Fortschritt");
            progressItem.setItemMeta(meta);
            inv.setItem(i, progressItem);
        }

        // Buttons
        inv.setItem(11, createMenuItem(Material.RED_BED, "§cSpawn 1 setzen"));
        inv.setItem(13, createMenuItem(Material.BLUE_BED, "§9Spawn 2 setzen"));
        inv.setItem(15, createMenuItem(Material.BEACON, "§aLobby setzen"));

        player.openInventory(inv);
        showBossBar(player);
    }

    private ItemStack createMenuItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    private void showBossBar(Player player) {
        // Entferne ggf. alte BossBar
        BossBar old = bossBars.remove(player.getUniqueId());
        if (old != null) {
            old.removeViewer(player);
        }

        BossBar bar = BossBar.bossBar(
                Component.text("Wähle Spawn 1, Spawn 2 oder die Lobby per Klick."),
                1.0f,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS);

        bar.addViewer(player);
        bossBars.put(player.getUniqueId(), bar);

        // Automatisch nach 10 Sekunden ausblenden
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bar.removeViewer(player);
            bossBars.remove(player.getUniqueId());
        }, 200L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player))
            return;
        if (!e.getView().getTitle().equals("§8Duel Setup"))
            return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return;

        String name = clicked.getItemMeta().getDisplayName();
        Location loc = player.getLocation();
        if (loc.getWorld() == null) {
            player.sendMessage("§cUngültige Welt!");
            return;
        }

        switch (name) {
            case "§cSpawn 1 setzen" -> {
                settings.setSpwan1(loc);
                player.sendMessage("§aSpawn 1 gespeichert!");
            }
            case "§9Spawn 2 setzen" -> {
                settings.setSpawn2(loc);
                player.sendMessage("§aSpawn 2 gespeichert!");
            }
            case "§aLobby setzen" -> {
                settings.setLobby(loc);
                player.sendMessage("§aLobby gespeichert!");
            }
            default -> {
                return;
            }
        }

        plugin.saveConfig();

        // Menü neu öffnen mit aktualisiertem Fortschritt
        Bukkit.getScheduler().runTaskLater(plugin, () -> openSetupMenu(player), 2L);

        // Abschlussnachricht
        if (settings.getSpawn1() != null && settings.getSpawn2() != null && settings.getLobby() != null) {

            Bukkit.getScheduler().runTaskLater(plugin, () -> playCompletionAnimation(player), 3L);

        }

    }

    private void playCompletionAnimation(Player player) {
        if (shownAnimation.contains(player.getUniqueId()))
            return;
        shownAnimation.add(player.getUniqueId());

        player.sendMessage("§aSetup abgeschlossen! Du kannst nun die Spawns und die Lobby verwenden.");

        // Titel + Untertitel
        player.sendTitle("§a✔ Setup abgeschlossen", "§7Du bist bereit!", 10, 40, 20);

        // Sound-Effekte
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.2f);

        // Feuerwerk (optional)
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.LIME)
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .flicker(true)
                .trail(true)
                .build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);

        // Fortschrittsbalken animieren (3x blinken)
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 6)
                    return;

                Inventory inv = Bukkit.createInventory(null, 27, "§8Duel Setup");
                for (int i = 0; i < 9; i++) {
                    ItemStack glass = new ItemStack(
                            count % 2 == 0 ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta meta = glass.getItemMeta();
                    meta.setDisplayName("§7Setup abgeschlossen");
                    glass.setItemMeta(meta);
                    inv.setItem(i, glass);
                }
                player.openInventory(inv);
                count++;
            }
        }, 0L, 10L); // alle 0.5 Sekunden
    }

}
