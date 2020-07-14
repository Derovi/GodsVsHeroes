package by.dero.gvh.minigame.commands;

import by.dero.gvh.PluginCommand;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import org.bukkit.command.CommandSender;

public class FinishCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (Minigame.getInstance().getGame().getState() == Game.State.WAITING ||
                Minigame.getInstance().getGame().getState() == Game.State.GAME_FULL) {
            sender.sendMessage("§cCan't finish game, not started yet!");
            return;
        }
        if (Minigame.getInstance().getGame().getState() == Game.State.PREPARING) {
            sender.sendMessage("§cCan't finish game, not prepared yet!");
            return;
        }
        boolean needFireworks = true;
        int winnerTeam;
        try {
            winnerTeam = Integer.parseInt(arguments[0]);
            if (arguments.length == 2) {
                needFireworks = Boolean.parseBoolean(arguments[1]);
            }
        } catch (Exception ex) {
            sender.sendMessage("§cInvalid arguments!");
            return;
        }
        Minigame.getInstance().getGame().finish(winnerTeam, needFireworks);
    }

    @Override
    public String getDescription() {
        return "<winner team> - finishes game forcibly if state is GAME.";
    }
}
