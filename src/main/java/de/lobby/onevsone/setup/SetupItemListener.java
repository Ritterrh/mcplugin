package de.lobby.onevsone.setup;

import de.lobby.LobbySystemMain;
import de.lobby.config.Settings;
import de.lobby.util.ChatUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
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

import java.time.Duration;
import java.util.*;

public class SetupItemListener implements Listener {

    private static final Component SETUP_TITLE = Component.text("Duel Setup", NamedTextColor.DARK_GRAY);
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
        if (e.getHand() != EquipmentSlot.HAND)
            return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta())
            return;
        Component name = item.getItemMeta().displayName();
        if (name == null || !PlainTextComponentSerializer.plainText().serialize(name).equals(SETUP_TOOL_NAME))
            return;
  

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
        if (!(e.getWhoClicked() instanceof Player player))
            return;
        if (!e.getView().title().equals(SETUP_TITLE))
            return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return;

        ItemMeta meta = clicked.getItemMeta();
        if (!meta.hasDisplayName())
            return;

        Component name = meta.displayName();
        if (name == null)
            return;

        String plain = PlainTextComponentSerializer.plainText().serialize(name);

        switch (plain) {
            case "Spawn 1 setzen" -> {
                settings.setSpawn1(player.getLocation());
                player.sendMessage(ChatUtil.success("Spawn 1 gespeichert!"));
            }
            case "Spawn 2 setzen" -> {
                settings.setSpawn2(player.getLocation());
                player.sendMessage(ChatUtil.success("Spawn 2 gespeichert!"));
            }
            case "Lobby setzen" -> {
                settings.setLobby(player.getLocation());
                player.sendMessage(ChatUtil.success("Lobby gespeichert!"));
            }
            default -> {
                return;
            }
        }

        plugin.saveConfig();
        Bukkit.getScheduler().runTaskLater(plugin, () -> openSetupMenu(player), 2L);
        
        if (settings.getSpawn1() != null && settings.getSpawn2() != null && settings.getLobby() != null) {
            if (!completed.contains(player.getUniqueId())) {
                completed.add(player.getUniqueId());
                Bukkit.getScheduler().runTaskLater(plugin, () ->{
                    player.closeInventory();
                    playCompletionAnimation(player);
                    player.getInventory().removeItem(new ItemStack(Material.BLAZE_ROD));
                    player.sendMessage(ChatUtil.success("Setup abgeschlossen! Du kannst nun starten."));
                }, 3L);
            }
        }
    }

    private void playCompletionAnimation(Player player) {
        player.sendMessage(ChatUtil.success("Setup abgeschlossen! Du kannst nun starten."));
        player.showTitle(Title.title(
                Component.text("✔ Setup abgeschlossen", NamedTextColor.GREEN),
                Component.text("Du bist bereit!", NamedTextColor.GRAY),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))));
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
