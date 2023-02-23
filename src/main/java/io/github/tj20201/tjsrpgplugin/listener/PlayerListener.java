package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
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
                        .replace("{mana}", plugin.getPlayerData(event.getPlayer(), "mana")+"/"+plugin.getPlayerData(event.getPlayer(), "maxMana"))
                        .replace("{level}", plugin.getPlayerData(event.getPlayer(), "level")+" ("+plugin.getPlayerData(event.getPlayer(), "curEXP")+"/"+plugin.getPlayerData(event.getPlayer(), "totEXP")+")")
                        ))));
                if (plugin.getPlayerData(event.getPlayer(), "curEXP") >= plugin.getPlayerData(event.getPlayer(), "totEXP")) {
                    int newLevel = plugin.getPlayerData(event.getPlayer(), "level")+1;
                    plugin.setPlayerData(event.getPlayer(), "level", newLevel);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(plugin.prefix+"You levelled up to level "+newLevel));
                    int oldRequiredEXP = plugin.getPlayerData(event.getPlayer(), "totEXP");
                    plugin.updatePlayerData(event.getPlayer(), oldRequiredEXP);
                    if (newLevel % 50 == 0) {
                        plugin.getServer().broadcastMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "&eUser &6"+event.getPlayer().getName()+"&e has reached Level &6"+newLevel+"&e!"));
                        plugin.dropRandomReward(event.getPlayer().getLocation(), event.getPlayer().getWorld());
                    }
                }
                plugin.setPlayerData(event.getPlayer(), "maxMana", plugin.getConfig().getInt("startingValues.mana")+(15*plugin.getPlayerData(event.getPlayer(), "level")));
                if (plugin.getPlayerData(event.getPlayer(), "maxMana") > plugin.getConfig().getInt("maximumValues.mana")) {plugin.setPlayerData(event.getPlayer(), "maxMana", plugin.getConfig().getInt("maximumValues.mana"));}
                if (plugin.getPlayerData(event.getPlayer(), "mana") > plugin.getPlayerData(event.getPlayer(), "maxMana")) {plugin.setPlayerData(event.getPlayer(), "mana", plugin.getPlayerData(event.getPlayer(), "maxMana"));}
                if (plugin.getPlayerData(event.getPlayer(), "level") > plugin.getConfig().getInt("levelLimit") && plugin.getConfig().getInt("levelLimit") != 0) {plugin.setPlayerData(event.getPlayer(), "level", plugin.getConfig().getInt("levelLimit"));}
            }
        }.runTaskTimer(plugin, 5L, 5L);
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
                plugin.setPlayerData(player, "curEXP", plugin.getPlayerData(player, "curEXP")+amountEXPToGive);
                player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(plugin.prefix+"You picked up "+amountEXPToGive+" experience!"));
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        String itemName = Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName();
        List<String> itemLore = Objects.requireNonNull(event.getItem().getItemMeta().getLore());
        String colourYellow = ChatColor.translateAlternateColorCodes('&', "&e");
        if (itemName.endsWith("Mana Potion") && itemLore.get(0).endsWith("Restores Mana")) {
            if (itemName.contains(colourYellow+"Small")) {plugin.setPlayerData(event.getPlayer(), "mana", plugin.getPlayerData(event.getPlayer(), "mana")+10);}
            if (itemName.contains(colourYellow+"Normal")) {plugin.setPlayerData(event.getPlayer(), "mana", plugin.getPlayerData(event.getPlayer(), "mana")+25);}
            if (itemName.contains(colourYellow+"Large")) {plugin.setPlayerData(event.getPlayer(), "mana", plugin.getPlayerData(event.getPlayer(), "mana")+50);}
        }
    }
}
