package by.dero.gvh.game.commands;

import by.dero.gvh.Minigame;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.game.Game;
import org.bukkit.command.CommandSender;

public class FinishCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (Minigame.getInstance().getGame().getState() == Game.State.WAITING) {
            sender.sendMessage("§cCan't finish game, not started yet!");
            return;
        }
        if (Minigame.getInstance().getGame().getState() == Game.State.PREPARING) {
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
        Minigame.getInstance().getGame().finish(winnerTeam);
    }

    @Override
    public String getDescription() {
        return "<winner team> - finishes game forcibly if state is GAME.";
    }
}
