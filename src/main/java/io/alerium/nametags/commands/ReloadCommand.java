package io.alerium.nametags.commands;

import io.alerium.nametags.NametagsPlugin;
import io.alerium.nametags.groups.NametagManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ReloadCommand implements CommandExecutor {

    private final NametagsPlugin plugin;
    private final NametagManager manager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nametags.reload")) {
            sender.sendMessage(plugin.getConfiguration().getMessage("messages.noPermission"));
            return true;
        }

        manager.reload();
        sender.sendMessage(plugin.getConfiguration().getMessage("messages.reloaded"));
        return true;
    }

}
