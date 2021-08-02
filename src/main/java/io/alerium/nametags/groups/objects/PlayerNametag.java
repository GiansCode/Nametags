package io.alerium.nametags.groups.objects;

import org.bukkit.entity.Player;

public class PlayerNametag extends NametagGroup {

    public PlayerNametag(String id, String prefix, String suffix, int priority, int sort) {
        super(id, prefix, suffix, priority, sort);
    }

    @Override
    public boolean canBeApplied(Player player) {
        return player.getUniqueId().toString().equals(id);
    }

}
