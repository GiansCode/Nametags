package io.alerium.nametags.groups;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.alerium.nametags.NametagsPlugin;
import io.alerium.nametags.groups.listeners.PlayerListener;
import io.alerium.nametags.groups.objects.NametagGroup;
import io.alerium.nametags.groups.objects.NametagTeam;
import io.alerium.nametags.groups.objects.PermissionNametag;
import io.alerium.nametags.groups.objects.PlayerNametag;
import io.alerium.nametags.groups.tasks.NametagUpdateTask;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NametagManager {

    private final NametagsPlugin plugin;

    private final Map<String, NametagGroup> groups = new HashMap<>();
    private final Map<String, NametagTeam> teams = new HashMap<>();
    private final Cache<UUID, String> uuidToGroup = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public void enable() {
        loadGroups();
        loadPlayers();
        plugin.getLogger().info("Loaded " + groups.size() + " nametags.");

        Bukkit.getPluginManager().registerEvents(new PlayerListener(plugin, this), plugin);
        new NametagUpdateTask(plugin, this).runTaskTimerAsynchronously(plugin, 20, plugin.getConfiguration().getConfig().getInt("refresh-interval"));
    }

    public void reload() {
        plugin.getConfiguration().reload();
        plugin.getGroupsConfig().reload();
        plugin.getPlayersConfig().reload();

        groups.clear();
        uuidToGroup.cleanUp();

        loadGroups();
        loadPlayers();
    }

    public void disable() {
        teams.values().forEach(team -> team.constructRemoveTeamPacket().broadcastPacket());
        teams.clear();
        groups.clear();
    }

    public void removePlayer(@NotNull Player player) {
        NametagTeam team = getTeam(player);
        if (team == null)
            return;

        WrapperPlayServerScoreboardTeam packet = team.constructRemovePlayerPacket(player.getName());
        if (team.getPlayers().isEmpty()) {
            team.constructRemoveTeamPacket().broadcastPacket();
            teams.remove(team.getId());
            return;
        }

        packet.broadcastPacket();
    }
    
    @NotNull
    public NametagGroup getNametagGroup(@NotNull Player player) {
        NametagGroup group = groups.get(uuidToGroup.getIfPresent(player.getUniqueId()));
        if (group != null)
            return group;

        group = checkNametagGroup(player);
        uuidToGroup.put(player.getUniqueId(), group.getId());
        return group;
    }

    @Nullable
    public NametagTeam getTeam(@NotNull Player player) {
        Optional<NametagTeam> optTeam = teams.values().stream().filter(team -> team.getPlayers().contains(player.getName())).findFirst();
        if (optTeam.isEmpty())
            return null;

        return optTeam.get();
    }

    @NotNull
    public NametagTeam getUpdatedTeam(@NotNull Player player) {
        NametagGroup group = getNametagGroup(player);
        String prefix = PlaceholderAPI.setPlaceholders(player, group.getPrefix());
        String suffix = PlaceholderAPI.setPlaceholders(player, group.getSuffix());

        Optional<NametagTeam> optTeam = teams.values().stream().filter(team -> team.isThisTeam(group.getSort(), prefix, suffix)).findFirst();
        if (optTeam.isPresent())
            return optTeam.get();

        String id = generateRandomTeamID(group.getSort());
        NametagTeam team = new NametagTeam(id, group.getSort(), prefix, suffix);
        teams.put(id, team);
        return team;
    }

    public void removeTeam(@NotNull NametagTeam team) {
        teams.remove(team.getId());
    }

    @NotNull
    public Collection<NametagTeam> getTeams() {
        return teams.values();
    }

    @NotNull
    private NametagGroup checkNametagGroup(@NotNull Player player) {
        NametagGroup newGroup = groups.get("default");
        for (NametagGroup group : groups.values()) {
            if (!group.canBeApplied(player))
                continue;

            if (newGroup == null || newGroup.getPriority() < group.getPriority())
                newGroup = group;
        }

        return newGroup;
    }

    @NotNull
    private String generateRandomTeamID(int sort) {
        StringBuilder sb = new StringBuilder();
        if (sort < 10)
            sb.append("00").append(sort);
        else if (sort < 100)
            sb.append("0").append(sort);
        else
            sb.append(sort);

        String s = sb.append(RandomStringUtils.randomAlphanumeric(10)).toString();
        if (teams.containsKey(s))
            return generateRandomTeamID(sort);

        return s;
    }

    private void loadGroups() {
        ConfigurationSection section = plugin.getGroupsConfig().getConfig().getConfigurationSection("groups");
        for (String id : section.getKeys(false)) {
            PermissionNametag nametag = new PermissionNametag(id, section.getString(id + ".prefix"), section.getString(id + ".suffix"), section.getInt(id + ".priority"), section.getInt(id + ".sort"));
            groups.put(id, nametag);
        }
    }

    private void loadPlayers() {
        ConfigurationSection section = plugin.getPlayersConfig().getConfig().getConfigurationSection("players");
        for (String id : section.getKeys(false)) {
            PlayerNametag nametag = new PlayerNametag(id, section.getString(id + ".prefix"), section.getString(id + ".suffix"), section.getInt(id + ".priority"), section.getInt(id + ".sort"));
            groups.put(id, nametag);
        }
    }

}
