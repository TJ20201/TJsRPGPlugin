package io.github.tj20201.tjsrpgplugin.listener;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EntityListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getDroppedExp() != 0) {
            ItemStack EXPOrb = new ItemStack(JavaPlugin.getPlugin(TJsRPGPlugin.class).EXPOrbMaterial);
            ItemMeta meta = EXPOrb.getItemMeta();
            assert meta != null;
            meta.setDisplayName(JavaPlugin.getPlugin(TJsRPGPlugin.class).EXPOrbName);
            meta.setLore(List.of(String.valueOf(event.getDroppedExp()), UUID.randomUUID().toString().replaceAll("-", "")));
            EXPOrb.setItemMeta(meta);
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), EXPOrb);
        }
    }
}
