package io.alerium.nametags.groups.listeners;

import io.alerium.nametags.NametagsPlugin;
import io.alerium.nametags.groups.NametagManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final NametagsPlugin plugin;
    private final NametagManager manager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> manager.getTeams().forEach(team -> {
            if (!team.getPlayers().contains(event.getPlayer().getName()))
                team.constructCreateTeamPacket().sendPacket(event.getPlayer());
        }), 5);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        manager.removePlayer(event.getPlayer());
    }

}
