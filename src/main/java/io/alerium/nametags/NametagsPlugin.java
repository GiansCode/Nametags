package io.alerium.nametags;

import io.alerium.nametags.commands.ReloadCommand;
import io.alerium.nametags.groups.NametagManager;
import io.alerium.nametags.utils.Configuration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class NametagsPlugin extends JavaPlugin {

    @Getter private static NametagsPlugin instance;

    @Getter private Configuration configuration;
    @Getter private Configuration groupsConfig;
    @Getter private Configuration playersConfig;

    private NametagManager nametagManager;

    @Override
    public void onEnable() {
        instance = this;

        configuration = new Configuration(this, "config");
        groupsConfig = new Configuration(this, "groups");
        playersConfig = new Configuration(this, "players");

        nametagManager = new NametagManager(this);
        nametagManager.enable();

        getCommand("nametagsreload").setExecutor(new ReloadCommand(this, nametagManager));
    }

    @Override
    public void onDisable() {
        nametagManager.disable();
    }

}
