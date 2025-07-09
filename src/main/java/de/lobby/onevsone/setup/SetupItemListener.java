package de.lobby.onevsone.setup;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import de.lobby.util.ChatUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SetupItemListener implements Listener {

    private static final String SETUP_TITLE = "§8Duel Setup";
    private static final Component SETUP_COMPONENT_TITLE = Component.text("Duel Setup", NamedTextColor.DARK_GRAY);
    private static final String SETUP_TOOL_NAME = "§6Duel-Setup-Werkzeug";

    private final LobbySystemMain plugin;
    private final Settings settings;
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final Set<UUID> completed = new HashSet<>();

    public SetupItemListener(LobbySystemMain plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        String displayName = item.getItemMeta().getDisplayName();
        if (!SETUP_TOOL_NAME.equals(displayName)) return;

        e.setCancelled(true);
        openSetupMenu(player);
        showBossBar(player);
    }

    private void openSetupMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, SETUP_TITLE);

        // Setup-Fortschritt (Slots 0-8)
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, createProgressItem(i));
        }

        // Aktionen (11,13,15)
        inv.setItem(11, createMenuItem(Material.RED_BED, "Spawn 1 setzen"));
        inv.setItem(13, createMenuItem(Material.BLUE_BED, "Spawn 2 setzen"));
        inv.setItem(15, createMenuItem(Material.BEACON, "Lobby setzen"));

        player.openInventory(inv);
    }

    private ItemStack createProgressItem(int slot) {
        boolean isSet = switch (slot) {
            case 2 -> settings.getSpawn1() != null;
            case 4 -> settings.getSpawn2() != null;
            case 6 -> settings.getLobby() != null;
            default -> false;
        };

        Material mat = isSet ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Setup-Fortschritt", NamedTextColor.GRAY));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createMenuItem(Material material, String label) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(label, NamedTextColor.YELLOW));
        item.setItemMeta(meta);
        return item;
    }

    private void showBossBar(Player player) {
        bossBars.computeIfAbsent(player.getUniqueId(), id -> {
            BossBar bar = BossBar.bossBar(
                    Component.text("Wähle Spawn 1, Spawn 2 oder die Lobby per Klick.", NamedTextColor.AQUA),
                    1.0f,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS);
            bar.addViewer(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                bar.removeViewer(player);
                bossBars.remove(player.getUniqueId());
            }, 200L);
            return bar;
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!SETUP_TITLE.equals(e.getView().getTitle())) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String displayName = clicked.getItemMeta().getDisplayName();
        Location loc = player.getLocation();

        switch (displayName) {
            case "Spawn 1 setzen" -> {
                settings.setSpawn1(loc);
                player.sendMessage(ChatUtil.success("Spawn 1 gespeichert!"));
            }
            case "Spawn 2 setzen" -> {
                settings.setSpawn2(loc);
                player.sendMessage(ChatUtil.success("Spawn 2 gespeichert!"));
            }
            case "Lobby setzen" -> {
                settings.setLobby(loc);
                player.sendMessage(ChatUtil.success("Lobby gespeichert!"));
            }
            default -> { return; }
        }

        plugin.saveConfig();

        // Menü aktualisieren
        Bukkit.getScheduler().runTaskLater(plugin, () -> openSetupMenu(player), 2L);

        if (settings.getSpawn1() != null && settings.getSpawn2() != null && settings.getLobby() != null) {
            if (!completed.contains(player.getUniqueId())) {
                completed.add(player.getUniqueId());
                Bukkit.getScheduler().runTaskLater(plugin, () -> playCompletionAnimation(player), 3L);
            }
        }
    }

    private void playCompletionAnimation(Player player) {
        player.sendMessage(ChatUtil.success("Setup abgeschlossen! Du kannst nun starten."));
        player.showTitle(net.kyori.adventure.title.Title.title(
                Component.text("✔ Setup abgeschlossen", NamedTextColor.GREEN),
                Component.text("Du bist bereit!", NamedTextColor.GRAY),
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(10 * 50),
                        java.time.Duration.ofMillis(40 * 50),
                        java.time.Duration.ofMillis(20 * 50)
                )
        ));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.LIME)
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}
