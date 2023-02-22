package io.github.tj20201.tjsrpgplugin.listener;

import com.google.common.collect.Multimap;
import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
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
