package io.github.tj20201.tjsrpgplugin.menu;

import io.github.tj20201.tjsrpgplugin.TJsRPGPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class MenuGUI {
    private final TJsRPGPlugin plugin;
    private final String title;

    private final Inventory inventory;

    public MenuGUI(String menuTitle, int menuRows) {
        this.plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        this.title = menuTitle;
        if (menuRows > 6) menuRows = 6;
        if (menuRows < 1) menuRows = 1;

        this.inventory = plugin.getServer().createInventory(null, menuRows*9, title);
    }

    private ItemStack guiLockItem(ItemStack item) {
        ItemStack newItem = item.clone();
        ItemMeta newItemMeta = newItem.getItemMeta();
        assert newItemMeta != null;
        List<String> newItemLore = newItemMeta.getLore();
        if (newItemLore != null) newItemLore.add("GUI LOCK"); else newItemLore = List.of("", "GUI LOCK");
        newItemMeta.setLore(newItemLore);
        newItem.setItemMeta(newItemMeta);
        return newItem;
    }

    @SuppressWarnings("unused") public void fillWithItem(ItemStack item) {
        item = guiLockItem(item);
        try {
            for (int slotItem = 0; slotItem < inventory.getSize(); slotItem++) {
                inventory.setItem(slotItem, item);
            }
        } catch (Exception exc) {
            plugin.getLogger().log(Level.WARNING, "An error has occurred in TJsRPGPlugin. Please report this with your version number and the following message: "+exc.getMessage()+" ("+exc.getCause()+")");
        }
    }
    @SuppressWarnings("unused") public void setItem(int index, ItemStack item) {
        item = guiLockItem(item);
        try {
            inventory.setItem(index, item);
        } catch (Exception exc) {
            plugin.getLogger().log(Level.WARNING, "An error has occurred in TJsRPGPlugin. Please report this with your version number and the following message: "+exc.getMessage()+" ("+exc.getCause()+")");
        }
    }

    @SuppressWarnings("unused") public String getTitle() {return title;}
    @SuppressWarnings("unused") public Inventory getInventory() {return inventory;}
    @SuppressWarnings("unused") public void showGUI(Player player) {player.openInventory(inventory);}
    @SuppressWarnings("unused") public void hideGUI(Player player) {if (player.getOpenInventory().getTitle().equals(title)) player.closeInventory();}
}
