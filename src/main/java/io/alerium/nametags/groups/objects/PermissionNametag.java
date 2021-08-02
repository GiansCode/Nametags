package io.alerium.nametags.groups.objects;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PermissionNametag extends NametagGroup {

    public PermissionNametag(String id, String prefix, String suffix, int priority, int sort) {
        super(id, prefix, suffix, priority, sort);
    }

    @Override
    public boolean canBeApplied(Player player) {
        return player.hasPermission("nametags.groups." + id);
    }

}
