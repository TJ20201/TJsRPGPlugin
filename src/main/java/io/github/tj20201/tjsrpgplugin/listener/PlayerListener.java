package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        FileConfiguration config = plugin.getConfig();
        if (!Objects.equals(config.getString("joinMessage"), "none")) {
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("joinMessage")).replace("{player}", event.getPlayer().getName())));
        }
        plugin.fixPlayerDataNullValues(event.getPlayer());
        try {
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
                        Object[][] unlockedSpells = plugin.getSpellsForLevel(newLevel, true);
                        if (unlockedSpells.length != 0) {
                            ArrayList<String> ulSpells = new ArrayList<>();
                            for (Object[] spell : unlockedSpells) {
                                ulSpells.add((String) spell[0]);
                            }
                            event.getPlayer().spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(plugin.prefix+"You unlocked the following spells: "+ulSpells));
                        }
                        if (newLevel % 50 == 0) {
                            plugin.getServer().broadcastMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "&eUser &6"+event.getPlayer().getName()+"&e has reached Level &6"+newLevel+"&e!"));
                            plugin.dropRandomReward(event.getPlayer().getLocation(), event.getPlayer().getWorld());
                        }
                    }
                    plugin.setPlayerData(event.getPlayer(), "maxMana", plugin.getConfig().getInt("startingValues.mana")+(15*plugin.getPlayerData(event.getPlayer(), "level")));
                    if (plugin.getPlayerData(event.getPlayer(), "maxMana") > plugin.getConfig().getInt("maximumValues.mana")) {plugin.setPlayerData(event.getPlayer(), "maxMana", plugin.getConfig().getInt("maximumValues.mana"));}
                    if (plugin.getPlayerData(event.getPlayer(), "mana") > plugin.getPlayerData(event.getPlayer(), "maxMana")) {plugin.setPlayerData(event.getPlayer(), "mana", plugin.getPlayerData(event.getPlayer(), "maxMana"));}
                    if (plugin.getPlayerData(event.getPlayer(), "level") > plugin.getConfig().getInt("levelLimit") && plugin.getConfig().getInt("levelLimit") != 0) {plugin.setPlayerData(event.getPlayer(), "level", plugin.getConfig().getInt("levelLimit"));}
                    Inventory inventory = event.getPlayer().getInventory();
                    for (ItemStack slotItem : inventory.getContents()) {
                        if (slotItem != null) {
                            ItemMeta slotItemMeta = slotItem.getItemMeta();
                            assert slotItemMeta != null;
                            List<String> slotItemLore = slotItemMeta.getLore();
                            if (slotItemLore != null) {
                                for (String lore : slotItemLore) {
                                    if (lore.toLowerCase().contains("gui lock")) {
                                        inventory.remove(slotItem);
                                    }
                                }
                            }
                        }
                    }
                }
            }.runTaskTimer(plugin, 5L, 5L);
        } catch (Exception exc) {
            plugin.getLogger().log(Level.SEVERE, "An error has occurred within TJsRPGPlugin. Message: "+exc.getMessage());
            event.getPlayer().kickPlayer("An error has occurred within TJsRPGPlugin. Please report the following to "+plugin.getDescription().getWebsite()+" with the following message: "+exc.getMessage()+" ("+exc.getCause()+")");
        }
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
        if (itemName.endsWith("Health Potion") && itemLore.get(0).endsWith("Restores Health")) {
            if (itemName.contains(colourYellow+"Small")) {event.getPlayer().setHealth(event.getPlayer().getHealth()+4);}
            if (itemName.contains(colourYellow+"Normal")) {event.getPlayer().setHealth(event.getPlayer().getHealth()+8);}
            if (itemName.contains(colourYellow+"Large")) {event.getPlayer().setHealth(event.getPlayer().getHealth()+16);}
        }
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        event.setCancelled(true);
        ItemMeta eventItemMeta = Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand()).getItemMeta();
        assert eventItemMeta != null;
        String itemName = eventItemMeta.getDisplayName();
        if (itemName.equals(Objects.requireNonNull(plugin.SpellWandItem.getItemMeta()).getDisplayName())) {
            Inventory gui = plugin.getServer().createInventory(null, InventoryType.CHEST);
            Object[][] playerSpells = plugin.getSpellsForLevel(plugin.getPlayerData(event.getPlayer(), "level"), false);
            int guiSlot = 0;
            while (gui.firstEmpty() != -1) {
                ItemStack blankItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta blankItemMeta = blankItem.getItemMeta();
                assert blankItemMeta != null;
                blankItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cLocked"));
                blankItemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Unlock by levelling up"), "", "GUI LOCK"));
                blankItem.setItemMeta(blankItemMeta);
                gui.setItem(guiSlot, blankItem);
                guiSlot += 1;
            }
            guiSlot = 0;
            for (Object[] playerSpell : playerSpells) {
                ItemStack displayItem = new ItemStack((Material) playerSpell[3]);
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                assert displayItemMeta != null;
                displayItemMeta.setDisplayName(playerSpell[0]+" ("+playerSpell[1]+")");
                displayItemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Mana Cost: "+playerSpell[4]), "", "SPELL ITEM", "GUI LOCK"));
                displayItem.setItemMeta(displayItemMeta);
                gui.setItem(guiSlot, displayItem);
                guiSlot += 1;
            }
            event.getPlayer().openInventory(gui);
        }
    }

    @EventHandler
    public void onPlayerInteractContainerItem(InventoryClickEvent event) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        ItemStack eventItem = event.getCurrentItem();
        HumanEntity eventEntity = event.getWhoClicked();
        Player eventPlayer = eventEntity.getServer().getPlayerExact(eventEntity.getName());
        if (eventItem != null) {
            ItemMeta eventItemMeta = eventItem.getItemMeta();
            if (eventItemMeta != null && eventItemMeta.hasLore()) {
                List<String> eventItemLore = eventItemMeta.getLore();
                assert eventItemLore != null;
                if (eventItemLore.contains("SPELL ITEM")) {
                    eventEntity.getOpenInventory().close();
                    int spellManaCost = Integer.parseInt(eventItemLore.get(0).split(": ")[1]);
                    assert eventPlayer != null;
                    plugin.setPlayerData(eventPlayer, "mana", plugin.getPlayerData(eventPlayer, "mana")-spellManaCost);
                    String spellName = ChatColor.stripColor(eventItemMeta.getDisplayName().split(" \\(")[0]).toLowerCase();
                    if (spellName.equals("fireball")) {
                        Fireball fireball = eventPlayer.getWorld().spawn(eventPlayer.getEyeLocation(), Fireball.class);
                        fireball.setYield(0);
                        fireball.setCustomName(eventPlayer.getName()+"'s Fireball");
                        fireball.setVelocity(eventPlayer.getLocation().getDirection());
                        new BukkitRunnable() {int ticks = 0;@Override public void run() {if (!fireball.isDead()) {fireball.getWorld().spawnParticle(Particle.FLAME, fireball.getLocation(), 1);if (ticks >= 80) {fireball.remove();}} else {cancel();}ticks += 1;}}.runTaskTimer(plugin, 0L, 1L);
                    }
                    else {return;}
                    eventEntity.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "You cast &9"+spellName+" &7for &9"+spellManaCost+"&7 mana."));
                }
            }
        }
    }
}
