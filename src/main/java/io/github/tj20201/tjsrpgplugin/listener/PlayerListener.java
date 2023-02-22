package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        FileConfiguration config = plugin.getConfig();
        if (!Objects.equals(config.getString("joinMessage"), "none")) {
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("joinMessage")).replace("{player}", event.getPlayer().getName())));
        }
        plugin.fixPlayerDataNullValues(event.getPlayer());
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!event.getPlayer().isOnline()) cancel();
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(config.getString("actionBarFormat"))
                        .replace("{mana}", plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "mana"), PersistentDataType.INTEGER)+"/"+plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER))
                        .replace("{level}", plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)+" ("+plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER)+"/"+plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "totEXP"), PersistentDataType.INTEGER)+")")
                        ))));
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER) >= plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "totEXP"), PersistentDataType.INTEGER)) {
                    int newLevel = plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)+1;
                    plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, newLevel);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(plugin.prefix+"You levelled up to level "+newLevel));
                    int oldRequiredEXP = plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "totEXP"), PersistentDataType.INTEGER);
                    plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER)-oldRequiredEXP);
                    plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "totEXP"), PersistentDataType.INTEGER, 100+(15*newLevel));
                    plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER)+(15*newLevel));
                }
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER) > plugin.getConfig().getInt("maximumValues.mana")) {plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER, plugin.getConfig().getInt("maximumValues.mana"));}
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "mana"), PersistentDataType.INTEGER) > plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER)) {plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "mana"), PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER));}
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxHealth"), PersistentDataType.INTEGER) > plugin.getConfig().getInt("maximumValues.health")) {plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxMana"), PersistentDataType.INTEGER, plugin.getConfig().getInt("maximumValues.health"));}
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "health"), PersistentDataType.INTEGER) > plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxHealth"), PersistentDataType.INTEGER)) {plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxHealth"), PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "maxHealth"), PersistentDataType.INTEGER));}
                if (plugin.getPlayerData(event.getPlayer(), new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER) > plugin.getConfig().getInt("levelLimit") && plugin.getConfig().getInt("levelLimit") != 0) {plugin.setPlayerData(event.getPlayer(), new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, plugin.getConfig().getInt("levelLimit"));}
            }
        }.runTaskTimerAsynchronously(plugin, 5L, 5L);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        FileConfiguration config = plugin.getConfig();
        if (!Objects.equals(config.getString("quitMessage"), "none")) {
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("quitMessage")).replace("{player}", event.getPlayer().getName())));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        FileConfiguration config = plugin.getConfig();
        if (!Objects.equals(config.getString("talkMessage"), "none")) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("talkMessage")).replace("{player}", event.getPlayer().getName()).replace("{message}", event.getMessage())));
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = plugin.getServer().getPlayer(event.getEntity().getName());
            if (plugin.checkItemIsEXPOrb(event.getItem())) {
                int amountEXPToGive = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getItem().getItemStack().getItemMeta()).getLore()).get(0))*event.getItem().getItemStack().getAmount();
                event.setCancelled(true);
                event.getItem().remove();
                assert player != null;
                plugin.setPlayerData(player, new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER, plugin.getPlayerData(player, new NamespacedKey(plugin, "curEXP"), PersistentDataType.INTEGER)+amountEXPToGive);
                player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(plugin.prefix+"You picked up "+amountEXPToGive+" experience!"));
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        NamespacedKey keyMana = new NamespacedKey(plugin, "mana");
        String itemName = Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName();
        List<String> itemLore = Objects.requireNonNull(event.getItem().getItemMeta().getLore());
        if (itemName.endsWith("Mana Potion") && itemLore.get(0).endsWith("Restores Mana")) {
            if (itemName.contains("Small")) {plugin.setPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER)+10);}
            if (itemName.contains("Normal")) {plugin.setPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER)+25);}
            if (itemName.contains("Large")) {plugin.setPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER, plugin.getPlayerData(event.getPlayer(), keyMana, PersistentDataType.INTEGER)+50);}
            event.getItem().setAmount(event.getItem().getAmount()-1);
        }
    }
}
