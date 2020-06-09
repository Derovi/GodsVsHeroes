package by.dero.gvh;

import org.bukkit.command.CommandSender;

public interface PluginCommand {
    void execute(CommandSender sender, String[] arguments);

    String getDescription();
}
