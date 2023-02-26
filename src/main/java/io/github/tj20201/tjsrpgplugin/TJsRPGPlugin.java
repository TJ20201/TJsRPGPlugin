package io.github.tj20201.tjsrpgplugin;

import io.github.tj20201.tjsrpgplugin.listener.EntityListener;
import io.github.tj20201.tjsrpgplugin.listener.PlayerListener;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
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

    private File playersDataFile;
    private FileConfiguration playersData;

    public ItemStack SpellWandItem;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        createPlayersData();
        for (Listener listener : listeners) {
            try {
                getServer().getPluginManager().registerEvents(listener.getClass().getDeclaredConstructor().newInstance(), this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        SpellWandItem = new ItemStack(Material.STICK);
        ItemMeta SpellWandItemMeta = SpellWandItem.getItemMeta();
        assert SpellWandItemMeta != null;
        SpellWandItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSpell Wand"));
        SpellWandItemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Swap Hands whilst holding to use"), "", UUID.randomUUID().toString().replace("-", "")));
        SpellWandItem.setItemMeta(SpellWandItemMeta);
        SpellWandItem.setAmount(1);

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createPlayersData() {
        playersDataFile = new File(getDataFolder(), "players.yml");
        if (!playersDataFile.exists()) {
            playersDataFile.getParentFile().mkdirs();
            saveResource("players.yml", false);
        }
        playersData = new YamlConfiguration();
        try {
            playersData.load(playersDataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private Object[][] addSpell(Object[] fSpell, Object[][] foundSpells) {
        ArrayList<Object[]> newFoundSpells = new ArrayList<>(Arrays.asList(foundSpells));
        newFoundSpells.add(fSpell);
        foundSpells = newFoundSpells.toArray(new Object[0][]);
        return foundSpells;
    }

    public Object[][] getSpellsForLevel(int level, boolean levelSpecific) {
        // Spells in format of {String name, String element, Integer level, Material displayItem, Integer manaCost, String description}
        Object[][] spells = {
                {ChatColor.translateAlternateColorCodes('&', "&cFireball&7"), "Fire", 1, Material.FIRE_CHARGE, 3, "Shoot a Fireball at your enemies."},
        };
        Object[][] foundSpells = new Object[0][];
        for (Object[] spell : spells) {
            if ((int) spell[2] == level) {foundSpells = addSpell(spell, foundSpells);}
            if ((int) spell[2] < level && !levelSpecific) {foundSpells = addSpell(spell, foundSpells);}
        }
        return foundSpells;
    }

    public FileConfiguration getPlayersData() {
        return this.playersData;
    }

    public void savePlayersData() {
        try {
            getPlayersData().save(playersDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Integer getPlayerData(Player player, String key) {
        int ret = getPlayersData().getInt(player.getUniqueId() +"."+key);
        savePlayersData();
        return ret;
    }
    public void setPlayerData(Player player, String key, Object value) {
        getPlayersData().set(player.getUniqueId() +"."+key, value);
        savePlayersData();
    }

    public void fixPlayerDataNullValues(Player player) {
        if (Objects.equals(getPlayerData(player, "level"), -1)) {setPlayerData(player, "level", getConfig().get("levelStart"));}
        if (Objects.equals(getPlayerData(player, "curEXP"), -1)) {setPlayerData(player, "curEXP", 0);}
        if (Objects.equals(getPlayerData(player, "totEXP"), -1)) {setPlayerData(player, "totEXP", 100);}
        if (Objects.equals(getPlayerData(player, "mana"), -1)) {setPlayerData(player, "mana", getConfig().get("startingValues.mana"));}
        if (Objects.equals(getPlayerData(player, "maxMana"), -1)) {setPlayerData(player, "maxMana", getConfig().get("startingValues.mana"));}
    }

    public boolean checkItemIsEXPOrb(Item item) {
        if (Objects.equals(Objects.requireNonNull(item.getItemStack().getItemMeta()).getDisplayName(), EXPOrbName)) {
            return Objects.equals(item.getItemStack().getType(), EXPOrbMaterial);
        } else {return false;}
    }

    public ItemStack[] getCustomItems() {
        String[] ItemNames = {"Mana Potion", "Health Potion"};
        String[] ItemColours = {"&e", "&e"};
        Material[] ItemMaterials = {Material.POTION, Material.POTION,};
        String[][] ItemModifiers = {{"Small", "Normal", "Large"},{"Small", "Normal", "Large"}};
        String[][] ItemDescriptions = {
                {"&7Restores Mana", "&7", "&7Small: 10 Mana", "&7Normal: 25 Mana", "&7Large: 50 Mana"},
                {"&7Restores Health", "&7", "&7Small: 2 Health", "&7Normal: 4 Health", "&7Large: 8 Health"}
        };
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

        this.setPlayerData(player, "curEXP", this.getPlayerData(player, "curEXP")-experienceToRemove);
        this.setPlayerData(player, "totEXP", 100+(15*this.getPlayerData(player, "level")));
        if (this.getPlayerData(player, "mana") > this.getPlayerData(player, "maxMana")) {this.setPlayerData(player, "mana", this.getPlayerData(player, "maxMana"));}
        if (this.getPlayerData(player, "mana") < 0) {this.setPlayerData(player, "mana", 0);}
    }
}
