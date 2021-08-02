package io.alerium.nametags.groups.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public abstract class NametagGroup {

    protected final String id;
    protected final String prefix;
    protected final String suffix;
    protected final int priority;
    protected final int sort;

    public abstract boolean canBeApplied(Player player);

}
