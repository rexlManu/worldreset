package de.rexlmanu.worldreset;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import lombok.var;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

// help command
// config reload
// add world
// remove world
// list world
public class WorldResetCommand implements CommandExecutor, TabExecutor {

    private WorldResetPlugin plugin;

    WorldResetCommand(WorldResetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        switch (arguments.length) {
            case 0:
                commandSender.sendMessage(
                        WorldResetPlugin.PREFIX + "Plugin version " + plugin.getDescription().getVersion());
                commandSender.sendMessage("https://www.spigotmc.org/resources/");
                break;
            case 1:
                switch (arguments[0].toLowerCase()) {
                    case "help":
                        commandSender.sendMessage(WorldResetPlugin.PREFIX + "Following Subcommands are available:");
                        commandSender.sendMessage("§7/wr help");
                        commandSender.sendMessage("§7/wr reload");
                        commandSender.sendMessage("§7/wr list");
                        commandSender.sendMessage("§7/wr add <World Name>");
                        commandSender.sendMessage("§7/wr remove <World Name>");
                        break;
                    case "reload":
                        this.plugin.reloadConfig();
                        commandSender.sendMessage(WorldResetPlugin.PREFIX + "The config was §asuccessful §7reloaded.");
                        break;
                    case "list":
                        commandSender.sendMessage(WorldResetPlugin.PREFIX + "Following worlds are added:");
                        for (String worldName : this.plugin.getConfiguration()
                                .getStringList(WorldResetPlugin.WORLDS_CONFIGURATION)) {
                            commandSender.sendMessage("§7" + worldName);
                        }
                        break;
                    default:
                        commandSender.sendMessage(WorldResetPlugin.PREFIX + "For further information: /wr help");
                        break;
                }
                break;
            case 2:
                switch (arguments[0].toLowerCase()) {
                    case "add": {
                        var worlds = this.plugin.getConfiguration()
                                .getStringList(WorldResetPlugin.WORLDS_CONFIGURATION);
                        if (worlds.contains(arguments[1])) {
                            commandSender.sendMessage(WorldResetPlugin.PREFIX + "This world is already added.");
                            break;
                        }
                        worlds.add(arguments[1]);
                        this.plugin.getConfiguration().set(WorldResetPlugin.WORLDS_CONFIGURATION, worlds);
                        this.plugin.saveConfig();
                        commandSender
                                .sendMessage(WorldResetPlugin.PREFIX + "The world §a" + arguments[1] + " §7was added.");
                    }
                        break;
                    case "remove": {
                        var worlds = this.plugin.getConfiguration()
                                .getStringList(WorldResetPlugin.WORLDS_CONFIGURATION);
                        if (!worlds.contains(arguments[1])) {
                            commandSender.sendMessage(WorldResetPlugin.PREFIX + "This world is not added to list.");
                            break;
                        }
                        worlds.remove(arguments[1]);
                        this.plugin.getConfiguration().set(WorldResetPlugin.WORLDS_CONFIGURATION, worlds);
                        this.plugin.saveConfig();
                        commandSender.sendMessage(
                                WorldResetPlugin.PREFIX + "The world §a" + arguments[1] + " §7was removed.");
                    }
                        break;
                    default:
                        commandSender.sendMessage(WorldResetPlugin.PREFIX + "For further information: /wr help");
                        break;
                }
                break;
            default:
                commandSender.sendMessage(WorldResetPlugin.PREFIX + "For further information: /wr help");
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return Arrays.asList("help", "reload", "list", "add", "remove");
            case 2:
                switch (arguments[0].toLowerCase()) {
                    case "add":
                        return Bukkit.getWorlds().stream().map(world -> world.getName())
                                .filter(worldName -> !this.plugin.getConfiguration()
                                        .getStringList(WorldResetPlugin.WORLDS_CONFIGURATION).contains(worldName))
                                .collect(Collectors.toList());
                    case "remove":
                        return this.plugin.getConfiguration().getStringList(WorldResetPlugin.WORLDS_CONFIGURATION);
                    default:
                        break;
                }
            default:
                break;
        }
        return null;
    }

}
