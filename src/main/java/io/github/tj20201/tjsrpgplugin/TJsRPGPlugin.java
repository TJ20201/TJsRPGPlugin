package io.github.tj20201.tjsrpgplugin;

import io.github.tj20201.tjsrpgplugin.listener.EntityListener;
import io.github.tj20201.tjsrpgplugin.listener.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class TJsRPGPlugin extends JavaPlugin {

    public String EXPOrbName = ChatColor.translateAlternateColorCodes('&', "&aEXP Orb");
    public Material EXPOrbMaterial = Material.SHROOMLIGHT;

    public String prefix = ChatColor.translateAlternateColorCodes('&', "&b[&9TJsRPGPlugin&b] &7");

    public List<Listener> listeners = List.of(new EntityListener(), new PlayerListener());
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        for (Listener listener : listeners) {
            try {
                getServer().getPluginManager().registerEvents(listener.getClass().getDeclaredConstructor().newInstance(), this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Integer getPlayerData(Player player, NamespacedKey key, PersistentDataType type) {
        String data = Objects.requireNonNull(player.getPersistentDataContainer().get(key, type)).toString();
        if (data != null) return Integer.parseInt(data);
        return -1;
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setPlayerData(Player player, NamespacedKey key, PersistentDataType type, Object value) {
        player.getPersistentDataContainer().set(key, type, value);
    }

    public void fixPlayerDataNullValues(Player player) {
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER, 1);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER, 0);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER, 100);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER), -1)) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}
    }

    public boolean checkItemIsEXPOrb(Item item) {
        if (Objects.equals(Objects.requireNonNull(item.getItemStack().getItemMeta()).getDisplayName(), EXPOrbName)) {
            return Objects.equals(item.getItemStack().getType(), EXPOrbMaterial);
        } else {return false;}
    }

    public ItemStack[] getCustomItems() {
        String[] ItemNames = {"Mana Potion"};
        String[] ItemColours = {"&e"};
        Material[] ItemMaterials = {Material.POTION};
        String[][] ItemModifiers = {{"Small", "Normal", "Large"}};
        String[][] ItemDescriptions = {{"&7Restores Mana", "&7", "&7Small: 10 Mana", "&7Normal: 25 Mana", "&7Large: 50 Mana"}};
        int Iteration = 0;
        ItemStack[] Items = {};
        for (String ItemName : ItemNames) {
            ItemStack Item = new ItemStack(ItemMaterials[Iteration]);
            ItemMeta ItemMeta = Item.getItemMeta();
            assert ItemMeta != null;
            ItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f"+ItemColours[Iteration]+ItemModifiers[Iteration][new Random().nextInt(ItemModifiers[Iteration].length-1)]+" "+ItemName));
            ArrayList<String> ColouredLore = new ArrayList<>();
            for (String LoreEntry : ItemDescriptions[Iteration]) {ColouredLore.add(ChatColor.translateAlternateColorCodes('&', LoreEntry));}
            ItemMeta.setLore(ColouredLore);
            ItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            Item.setItemMeta(ItemMeta);
            Item.setAmount(1);
            ArrayList<ItemStack> ALItems = new ArrayList<>(Arrays.asList(Items));
            ALItems.add(Item);
            Items = ALItems.toArray(Items);
            Iteration += 1;
        }
        return Items;
    }
}
