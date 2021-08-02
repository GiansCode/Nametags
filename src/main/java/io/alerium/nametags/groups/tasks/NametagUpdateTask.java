package io.alerium.nametags.groups.tasks;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import io.alerium.nametags.NametagsPlugin;
import io.alerium.nametags.groups.NametagManager;
import io.alerium.nametags.groups.objects.NametagTeam;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NametagUpdateTask extends BukkitRunnable {

    private final NametagsPlugin plugin;
    private final NametagManager manager;

    @Override
    public void run() {
        List<AbstractPacket> packets = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            NametagTeam oldTeam = manager.getTeam(player);
            NametagTeam team = manager.getUpdatedTeam(player);
            if (oldTeam != null) {
                if (team == oldTeam)
                    continue;

                WrapperPlayServerScoreboardTeam packet = oldTeam.constructRemovePlayerPacket(player.getName());
                if (oldTeam.getPlayers().isEmpty()) {
                    packet = oldTeam.constructRemoveTeamPacket();
                    manager.removeTeam(oldTeam);
                }

                packets.add(packet);
            }

            if (team.getPlayers().isEmpty()) {
                team.getPlayers().add(player.getName());
                packets.add(team.constructCreateTeamPacket());
                continue;
            }

            if (!team.getPlayers().contains(player.getName()))
                packets.add(team.constructAddPlayerPacket(player.getName()));
        }

        Bukkit.getScheduler().runTask(plugin, () -> packets.forEach(AbstractPacket::broadcastPacket));
    }

}
