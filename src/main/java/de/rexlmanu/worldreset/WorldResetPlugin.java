package de.rexlmanu.worldreset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.var;

/**
 * 1. List of all worlds that should get reset. 2. Disable autosave for worlds
 * 3. On shutdown unload world
 */
@Getter
public class WorldResetPlugin extends JavaPlugin implements Listener {

    public static final String PREFIX = "§8» §bWorldReset §8• §7";
    public static final String WORLDS_CONFIGURATION = "worlds";

    private File configFile;
    private FileConfiguration configuration;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();
        this.configFile = new File(this.getDataFolder(), "worlds.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        this.configuration.addDefault(WORLDS_CONFIGURATION, new ArrayList<>());
        this.configuration.options().copyDefaults(true);

        this.configuration.getStringList(WORLDS_CONFIGURATION).stream().forEach(
                worldName -> Optional.of(Bukkit.getWorld(worldName)).ifPresent(world -> world.setAutoSave(false)));

        this.saveConfig();

        var command = new WorldResetCommand(this);
        this.getCommand("worldreset").setExecutor(command);
        this.getCommand("worldreset").setTabCompleter(command);
    }

    @Override
    public void reloadConfig() {
        try {
            this.configuration.load(this.configFile);
            this.getLogger().info("Worlds configuration was reloaded.");
        } catch (IOException e) {
            this.getLogger().severe("A error occuried while trying to load the worlds file.");
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe("The loaded configuration was invalid.");
        }
    }

    @Override
    public void saveConfig() {
        try {
            this.configuration.save(this.configFile);
            this.getLogger().info("Worlds configuration was saved.");
        } catch (IOException e) {
            this.getLogger().severe("A error occuried while trying to save the worlds file.");
        }
    }

    @Override
    public void onDisable() {
        configuration.getStringList(WORLDS_CONFIGURATION).stream()
                .forEach(worldName -> Bukkit.unloadWorld(worldName, false));
    }

}
