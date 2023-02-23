package io.github.tj20201.tjsrpgplugin;

import io.github.tj20201.tjsrpgplugin.listener.EntityListener;
import io.github.tj20201.tjsrpgplugin.listener.PlayerListener;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

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
        Objects.requireNonNull(this.getCommand("tjsrpgplugin")).setExecutor(new TJsRPGPluginCommand());
        Objects.requireNonNull(this.getCommand("tjsrpgplugin")).setTabCompleter(new TJsRPGPluginCommand());
                String[] version = getDescription().getVersion().split("\\.");
        try {
           URL url = new URL("https://raw.githubusercontent.com/TJ20201/TJsRPGPlugin/master/build.gradle");
           BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
           String inputLine;
           String[] lstVersion = new String[0];
           while ((inputLine = in.readLine()) != null) {
               if (inputLine.startsWith("version = ")) {
                   lstVersion = inputLine.split(" = ")[1].replace("'", "").split("\\.");
                   break;
               }
           }
           in.close();
           assert lstVersion.length != 0;
           // Lower version checking
           if (Integer.parseInt(version[2]) < Integer.parseInt(lstVersion[2])) {
               getLogger().log(Level.WARNING, "Running on a lower patch version than the latest stable release. Updating is not necessary, but recommended.");
           } else if (Integer.parseInt(version[1]) < Integer.parseInt(lstVersion[1])) {
               getLogger().log(Level.WARNING, "Running on a lower minor version than the latest stable release. Please update to the latest version at "+getDescription().getWebsite()+".");
           } else if (Integer.parseInt(version[0]) < Integer.parseInt(lstVersion[0])) {
               getLogger().log(Level.WARNING, "Running on a lower major version than the latest stable release. Please update to the latest version at "+getDescription().getWebsite()+".");
           }
           // Higher version checking
           if (Integer.parseInt(version[2]) > Integer.parseInt(lstVersion[2]) || Integer.parseInt(version[1]) > Integer.parseInt(lstVersion[1]) || Integer.parseInt(version[0]) > Integer.parseInt(lstVersion[0])) {
               getLogger().log(Level.WARNING, "Running on an unstable version of TJsRPGPlugin. Report any errors at "+getDescription().getWebsite()+" with your version number.");
           }
        }
        catch(IOException ex) {
           getLogger().log(Level.WARNING, "Unable to obtain the latest version number available online.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Integer getPlayerData(Player player, NamespacedKey key) {
        String data = Objects.requireNonNull(player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER)).toString();
        return Integer.parseInt(data);
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setPlayerData(Player player, NamespacedKey key, Object value) {
        player.getPersistentDataContainer().set(key, (PersistentDataType) PersistentDataType.INTEGER, value);
    }

    public void fixPlayerDataNullValues(Player player) {
        if (Objects.equals(getPlayerData(player, new NamespacedKey(this, "level")), -1)) {setPlayerData(player, new NamespacedKey(this, "level"), getConfig().get("levelStart"));}
        if (Objects.equals(getPlayerData(player, new NamespacedKey(this, "curEXP")), -1)) {setPlayerData(player, new NamespacedKey(this, "curEXP"), 0);}
        if (Objects.equals(getPlayerData(player, new NamespacedKey(this, "totEXP")), -1)) {setPlayerData(player, new NamespacedKey(this, "totEXP"), 100);}
        if (Objects.equals(getPlayerData(player, new NamespacedKey(this, "mana")), -1)) {setPlayerData(player, new NamespacedKey(this, "mana"), getConfig().get("startingValues.mana"));}
        if (Objects.equals(getPlayerData(player, new NamespacedKey(this, "maxMana")), -1)) {setPlayerData(player, new NamespacedKey(this, "maxMana"), getConfig().get("startingValues.mana"));}
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
            ItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f" + ItemColours[Iteration] + ItemModifiers[Iteration][new Random().nextInt(ItemModifiers[Iteration].length)] + " " + ItemName));
            ArrayList<String> ColouredLore = new ArrayList<>();
            for (String LoreEntry : ItemDescriptions[Iteration]) {
                ColouredLore.add(ChatColor.translateAlternateColorCodes('&', LoreEntry));
            }
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
    public void dropRandomReward(Location location, World world) {
        Material[] Rewards = {Material.NETHER_STAR, Material.NETHERITE_SCRAP, Material.TOTEM_OF_UNDYING};
        int[] RewardMaxCounts = {2, 6, 1};
        int Index = new Random().nextInt(Rewards.length);
        int SRCount;
        if (RewardMaxCounts[Index] == 1) {
            SRCount = 1;
        } else {
            SRCount = new Random().nextInt(RewardMaxCounts[Index])+1;
        }
        ItemStack Item = new ItemStack(Rewards[Index]);
        Item.setAmount(SRCount);
        ItemMeta ItemMeta = Item.getItemMeta();
        assert ItemMeta != null;
        ItemMeta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', "&7Received from TJ's RPG Plugin Level Milestones")));
        Item.setItemMeta(ItemMeta);
        world.dropItem(location, Item);
    }

    public Player getPlayer(String match) {
        ArrayList<String> matchedPlayers = new ArrayList<>();
        ArrayList<String> onlinePlayers = new ArrayList<>();
        for (Player onlinePlayer : this.getServer().getOnlinePlayers()) onlinePlayers.add(onlinePlayer.getName());
        StringUtil.copyPartialMatches(match, onlinePlayers, matchedPlayers);
        Collections.sort(matchedPlayers);
        return this.getServer().getPlayer(matchedPlayers.get(0));
    }

    public void updatePlayerData(Player player) {updatePlayerData(player, 0);}
    public void updatePlayerData(Player player, Integer experienceToRemove) {

        this.setPlayerData(player, new NamespacedKey(this, "curEXP"), this.getPlayerData(player, new NamespacedKey(this, "curEXP"))-experienceToRemove);
        this.setPlayerData(player, new NamespacedKey(this, "totEXP"), 100+(15*this.getPlayerData(player, new NamespacedKey(this, "level"))));
        if (this.getPlayerData(player, new NamespacedKey(this, "mana")) > this.getPlayerData(player, new NamespacedKey(this, "maxMana"))) {this.setPlayerData(player, new NamespacedKey(this, "mana"), this.getPlayerData(player, new NamespacedKey(this, "maxMana")));}
        if (this.getPlayerData(player, new NamespacedKey(this, "mana")) < 0) {this.setPlayerData(player, new NamespacedKey(this, "mana"), 0);}
    }
}
