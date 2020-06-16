package by.dero.gvh.minigame.commands;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.command.CommandSender;

public class SelectCommand implements PluginCommand {
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
        if (!Plugin.getInstance().getPlayerData().getPlayerInfo(sender.getName()).isClassUnlocked(arguments[0])) {
            sender.sendMessage("§cClass isn't unlocked!");
            return;
        }
        GamePlayer player = Minigame.getInstance().getGame().getPlayers().get(sender.getName());
        player.selectClass(arguments[0]);
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(arguments[0]);
        for (String itemName : classDescription.getItemNames()) {
            player.addItem(itemName, 0);
        }
        sender.sendMessage("§aSelected class: " + arguments[0]);
    }

    @Override
    public String getDescription() {
        return "<class> -  select class";
    }
}
