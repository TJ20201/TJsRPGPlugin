package io.github.tj20201.tjsrpgplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class TJsRPGPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
