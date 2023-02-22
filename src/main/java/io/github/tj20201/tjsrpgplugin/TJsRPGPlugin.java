package io.github.tj20201.tjsrpgplugin;

import io.github.tj20201.tjsrpgplugin.listener.EntityListener;
import io.github.tj20201.tjsrpgplugin.listener.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

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
    public String getPlayerData(Player player, NamespacedKey key, PersistentDataType type) {
        Object data = player.getPersistentDataContainer().get(key, type);
        if (data != null) return data.toString();
        return "Null";
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setPlayerData(Player player, NamespacedKey key, PersistentDataType type, Object value) {
        player.getPersistentDataContainer().set(key, type, value);
    }

    public void fixPlayerDataNullValues(Player player) {
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "health"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "level"), PersistentDataType.INTEGER, 1);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "curEXP"), PersistentDataType.INTEGER, 0);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "totEXP"), PersistentDataType.INTEGER, 100);}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "mana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxHealth"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.health"));}
        if (Objects.equals(JavaPlugin.getPlugin(TJsRPGPlugin.class).getPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER), "Null")) {JavaPlugin.getPlugin(TJsRPGPlugin.class).setPlayerData(player, new NamespacedKey(JavaPlugin.getPlugin(TJsRPGPlugin.class), "maxMana"), PersistentDataType.INTEGER, JavaPlugin.getPlugin(TJsRPGPlugin.class).getConfig().get("startingValues.mana"));}
    }

    public boolean checkItemIsEXPOrb(Item item) {
        if (Objects.equals(Objects.requireNonNull(item.getItemStack().getItemMeta()).getDisplayName(), EXPOrbName)) {
            return Objects.equals(item.getItemStack().getType(), EXPOrbMaterial);
        } else {return false;}
    }
}
