package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
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
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER, 1);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}

        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isOnline()) cancel();
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(config.getString("actionBarFormat"))
                        .replace("{mana}", JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER)+"/"+JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER))
                        .replace("{level}", JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(event.getPlayer(), new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER))
                        ))));
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
}
