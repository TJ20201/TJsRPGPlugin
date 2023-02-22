package io.github.tj20201.tjsrpgplugin;

import io.github.tj20201.tjsrpgplugin.listener.PlayerListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class TJsRPGPlugin extends JavaPlugin {

    public Class[] listeners = {PlayerListener.class};
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        for (Class listener : listeners) {
            try {
                getServer().getPluginManager().registerEvents((Listener) listener.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String getPlayerData(Player player, NamespacedKey key, PersistentDataType type) {
        Object data = player.getPersistentDataContainer().get(key, type);
        if (data != null) return data.toString();
        return "Null";
    }
    public void setPlayerData(Player player, NamespacedKey key, PersistentDataType type, Object value) {
        player.getPersistentDataContainer().set(key, type, value);
    }
}
