package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration config = JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig();
        if (!Objects.equals(config.getString("joinMessage"), "none")) {
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("joinMessage")).replace("{player}", event.getPlayer().getName())));
        }
        JavaPlugin.getPlugin(TJsRPGPlugin.class).fixPlayerDataNullValues(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isOnline()) cancel();
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(config.getString("actionBarFormat"))
                        .replace("{mana}", JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER)+"/"+JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER))
                        .replace("{level}", JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER)+" ("+JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER)+"/"+JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER)+")")
                        ))));
                if (Integer.valueOf(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER)) >= Integer.valueOf(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER))) {
                    int newLevel = Integer.parseInt(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER))+1;
                    JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER, newLevel);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(JavaPlugin.getPlugin(TJsRPGPlugin.class).prefix+"You levelled up to level "+newLevel));
                    int oldRequiredEXP = Integer.parseInt(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER));
                    JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER, Integer.parseInt(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER))-oldRequiredEXP);
                    JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER, 100+(15*newLevel));

                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(TJsRPGPlugin.class), 5L, 5L);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FileConfiguration config = JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig();
        if (!Objects.equals(config.getString("quitMessage"), "none")) {
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("quitMessage")).replace("{player}", event.getPlayer().getName())));
        }
    }

    @EventHandler
    public void onPlayerTalk(AsyncPlayerChatEvent event) {
        FileConfiguration config = JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig();
        if (!Objects.equals(config.getString("talkMessage"), "none")) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("talkMessage")).replace("{player}", event.getPlayer().getName()).replace("{message}", event.getMessage())));
        }
    }

    @EventHandler
    public void onPlayerPickupEXPItem(EntityPickupItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = JavaPlugin.getPlugin(TJsRPGPlugin.class).getServer().getPlayer(event.getEntity().getName());
            if (JavaPlugin.getPlugin(TJsRPGPlugin.class).checkItemIsEXPOrb(event.getItem())) {
                int amountEXPToGive = Integer.parseInt(event.getItem().getItemStack().getItemMeta().getLore().get(0));
                event.setCancelled(true);
                event.getItem().remove();
                assert player != null;
                JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER, Integer.valueOf(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER))+amountEXPToGive);
                player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(JavaPlugin.getPlugin(TJsRPGPlugin.class).prefix+"You picked up "+amountEXPToGive+" experience!"));
            }
        }
    }
}
