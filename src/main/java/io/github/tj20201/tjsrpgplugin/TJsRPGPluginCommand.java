package io.github.tj20201.tjsrpgplugin;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TJsRPGPluginCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        if (Objects.equals(args[0], "help")) {
            sender.sendMessage(plugin.prefix+"TJ's RPG Plugin is a plugin that adds common RPG features into Minecraft without the use of mods.");
            return true;
        }
        if (Objects.equals(args[0], "spellwand")) {
            sender.sendMessage(plugin.prefix+"Giving you a new Spell Wand...");
            Player senderAsPlayer = sender.getServer().getPlayer(sender.getName());assert senderAsPlayer != null;
            senderAsPlayer.getInventory().addItem(plugin.SpellWandItem);
            sender.sendMessage(plugin.prefix+"Successfully gave you a new Spell Wand.");
            return true;
        }
        if (Objects.equals(args[0], "admin")) {
            if (sender.hasPermission("tjsrpgplugin.admin")) {
                if (Objects.equals(args[1], "level")) {
                    Player target;
                    if (!Objects.equals(args[3], "<self>")) {
                        try {
                            target = plugin.getPlayer(args[3]);
                        } catch (ArrayIndexOutOfBoundsException exc) {
                            sender.sendMessage(plugin.prefix + "Missing a player argument at argument 4.");
                            return false;
                        }
                    } else {
                        target = plugin.getServer().getPlayerExact(sender.getName());
                    }
                    assert target != null;
                    if (Objects.equals(args[2], "set")) {
                        int oldLevel = plugin.getPlayerData(target, "level");
                        int newLevel = Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldLevel + "&7 to Level &b" + newLevel + "&7."));
                        plugin.setPlayerData(target, "level", newLevel);
                        plugin.setPlayerData(target, "curEXP", 0);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "add")) {
                        int oldLevel = plugin.getPlayerData(target, "level");
                        int newLevel = plugin.getPlayerData(target, "level")+Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldLevel + "&7 to Level &b" + newLevel + "&7."));
                        plugin.setPlayerData(target, "level", newLevel);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "remove")) {
                        int oldLevel = plugin.getPlayerData(target, "level");
                        int newLevel = plugin.getPlayerData(target, "level")-Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldLevel + "&7 to Level &b" + newLevel + "&7."));
                        plugin.setPlayerData(target, "level", newLevel);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "get")) {
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b"+target.getName()+"&7 is at level &b"+plugin.getPlayerData(target, "level")+"&7."));
                        return true;
                    }
                    else {
                        sender.sendMessage(plugin.prefix+"Missing a required subcommand at argument 3.");
                        return false;
                    }
                } else if (Objects.equals(args[1], "mana")) {
                    Player target;
                    if (!Objects.equals(args[3], "<self>")) {
                        try {
                            target = plugin.getPlayer(args[3]);
                        } catch (ArrayIndexOutOfBoundsException exc) {
                            sender.sendMessage(plugin.prefix + "Missing a player argument at argument 4.");
                            return false;
                        }
                    } else {
                        target = plugin.getServer().getPlayerExact(sender.getName());
                    }
                    assert target != null;
                    if (Objects.equals(args[2], "set")) {
                        int oldMana = plugin.getPlayerData(target, "mana");
                        int newMana = Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldMana + "&7 to Level &b" + newMana + "&7."));
                        plugin.setPlayerData(target, "mana", newMana);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "add")) {
                        int oldMana = plugin.getPlayerData(target, "mana");
                        int newMana = plugin.getPlayerData(target, "mana")+Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldMana + "&7 to Level &b" + newMana + "&7."));
                        plugin.setPlayerData(target, "mana", newMana);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "remove")) {
                        int oldMana = plugin.getPlayerData(target, "mana");
                        int newMana = plugin.getPlayerData(target, "mana") - Integer.parseInt(args[4]);
                        sender.sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', "User &b" + target.getName() + "&7 has been changed from Level &b" + oldMana + "&7 to Level &b" + newMana + "&7."));
                        plugin.setPlayerData(target, "mana", newMana);
                        plugin.updatePlayerData(target);
                        return true;
                    }
                    else if (Objects.equals(args[2], "get")) {
                        sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "User &b"+target.getName()+"&7 has &b"+plugin.getPlayerData(target, "mana")+"/"+plugin.getPlayerData(target, "maxMana")+"&7 mana."));
                        return true;
                    }
                    else {
                        sender.sendMessage(plugin.prefix+"Missing a required subcommand at argument 3.");
                        return false;
                    }
                }
                else {
                    sender.sendMessage(plugin.prefix+"Missing a required subcommand at argument 2.");
                    return false;
                }
            } else {
                sender.sendMessage(plugin.prefix+ChatColor.translateAlternateColorCodes('&', "You do not have permission to use the Admin subcommands. If you believe this is a mistake, make sure to contact the person managing permissions to ensure you have the &ctjsrpgplugin.admin &7permission node."));
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        TJsRPGPlugin plugin = JavaPlugin.getPlugin(TJsRPGPlugin.class);
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("tjsrpgplugin.admin")) commands.add("admin");
            commands.add("help");
            commands.add("spellwand");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (sender.hasPermission("tjsrpgplugin.admin")) {
                commands.add("level");
                commands.add("mana");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if (sender.hasPermission("tjsrpgplugin.admin")) {
                commands.add("set");
                commands.add("add");
                commands.add("remove");
                commands.add("get");
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4) {
            if (sender.hasPermission("tjsrpgplugin.admin")) {
                commands.add("<self>");
                for (Player player : plugin.getServer().getOnlinePlayers()) commands.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        } else if (args.length == 5) {
            if (sender.hasPermission("tjsrpgplugin.admin")) {
                if (!Objects.equals(args[2], "get")) {
                    commands.add("<integer>");
                }
            }
            StringUtil.copyPartialMatches(args[4], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
