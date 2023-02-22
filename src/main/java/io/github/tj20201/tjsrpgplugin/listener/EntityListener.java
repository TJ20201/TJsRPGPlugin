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
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        if (event.getDroppedExp() != 0) {
            ItemStack EXPOrb = new ItemStack(plugin.EXPOrbMaterial);
            ItemMeta meta = EXPOrb.getItemMeta();
            assert meta != null;
            meta.setDisplayName(plugin.EXPOrbName);
            meta.setLore(List.of(String.valueOf(event.getDroppedExp())));
            EXPOrb.setItemMeta(meta);
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), EXPOrb);
        }
        if (new Random().nextInt(9)+1 == 5) { // 1/10 chance for an item to spawn
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), plugin.getCustomItems()[new Random().nextInt(plugin.getCustomItems().length)]);
        }
    }
}
