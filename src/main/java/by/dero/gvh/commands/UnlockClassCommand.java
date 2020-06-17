package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginCommand;
import org.bukkit.command.CommandSender;


public class UnlockClassCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length != 1) {
            sender.sendMessage("§cInvalid arguments!");
            return;
        }
        if (!Plugin.getInstance().getData().getClassNameToDescription().containsKey(arguments[0])) {
            sender.sendMessage("§cClass not found!");
            return;
        }
        Plugin.getInstance().getPlayerData().unlockClass(sender.getName(), arguments[0]);
        sender.sendMessage("§aUnlocked class: " + arguments[0]);
    }

    @Override
    public String getDescription() {
        return "";
    }
}
