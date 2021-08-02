package io.alerium.nametags.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Configuration {

    private final Plugin plugin;
    private final File file;

    @Getter private FileConfiguration config;

    public Configuration(Plugin plugin, String name) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists())
            plugin.saveResource(file.getName(), false);

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while saving the config file.");
        }
    }

    public Component getMessage(String path) {
        return MiniMessage.get().deserialize(config.getString(path));
    }

}