package io.alerium.nametags.groups.objects;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NametagTeam {

    @Getter private final String id;
    private final int sort;
    private final String prefix;
    private final String suffix;
    @Getter private final List<String> players = new ArrayList<>();

    private WrappedChatComponent cachedPrefix;
    private WrappedChatComponent cachedSuffix;

    public boolean isThisTeam(int sort, String prefix, String suffix) {
        return this.sort == sort && this.prefix.equals(prefix) && this.suffix.equals(suffix);
    }

    public WrapperPlayServerScoreboardTeam constructCreateTeamPacket() {
        generateCachedData();

        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(id);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        packet.setDisplayName(WrappedChatComponent.fromText(""));
        packet.setPackOptionData(0x01);
        packet.setNameTagVisibility("always");
        packet.setCollisionRule("always");
        packet.setColor(ChatColor.WHITE);
        packet.setPrefix(cachedPrefix);
        packet.setSuffix(cachedSuffix);
        packet.setPlayers(players);
        return packet;
    }

    public WrapperPlayServerScoreboardTeam constructRemoveTeamPacket() {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(id);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED);
        return packet;
    }

    public WrapperPlayServerScoreboardTeam constructAddPlayerPacket(String name) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(id);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
        packet.setPlayers(Collections.singletonList(name));

        players.add(name);
        return packet;
    }

    public WrapperPlayServerScoreboardTeam constructRemovePlayerPacket(String name) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(id);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
        packet.setPlayers(Collections.singletonList(name));

        players.remove(name);
        return packet;
    }

    private void generateCachedData() {
        cachedPrefix = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.get().parse(prefix)));
        cachedSuffix = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.get().parse(suffix)));
    }

}
