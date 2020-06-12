package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.game.Game;
import org.bukkit.command.CommandSender;

public class FinishCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (Plugin.getInstance().getGame().getState() == Game.State.WAITING) {
            sender.sendMessage("§cCan't start game, not started yet!");
            return;
        }
        if (Plugin.getInstance().getGame().getState() == Game.State.PREPARING) {
            sender.sendMessage("§cCan't finish game, not prepared yet!");
            return;
        }
        int winnerTeam;
        try {
            winnerTeam = Integer.parseInt(arguments[0]);
        } catch (Exception ex) {
            sender.sendMessage("§cInvalid arguments!");
            return;
        }
        Plugin.getInstance().getGame().finish(winnerTeam);
    }

    @Override
    public String getDescription() {
        return "<winner team> - finishes game forcibly if state is GAME.";
    }
}
