package by.dero.gvh.minigame.commands;

import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.minigame.Game;
import org.bukkit.command.CommandSender;

public class StartCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (Minigame.getInstance().getGame().getState() == Game.State.GAME) {
            sender.sendMessage("§cCan't start game, already started!");
            return;
        }
        if (Minigame.getInstance().getGame().getState() == Game.State.PREPARING) {
            sender.sendMessage("§cCan't start game, not prepared yet!");
            return;
        }
        Minigame.getInstance().getGame().getLobby().startGame();
    }

    @Override
    public String getDescription() {
        return "- starts game forcibly if state is WAITING.";
    }
}
