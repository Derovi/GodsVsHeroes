package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.game.Game;
import org.bukkit.command.CommandSender;

public class StartCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (Plugin.getInstance().getGame().getState() == Game.State.GAME) {
            sender.sendMessage("§cCan't start game, already started!");
            return;
        }
        if (Plugin.getInstance().getGame().getState() == Game.State.PREPARING) {
            sender.sendMessage("§cCan't start game, not prepared yet!");
            return;
        }
        Plugin.getInstance().getGame().start();
    }

    @Override
    public String getDescription() {
        return "- starts game forcibly if state is WAITING.";
    }
}
