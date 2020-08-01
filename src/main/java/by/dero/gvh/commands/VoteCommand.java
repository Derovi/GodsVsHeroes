package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        if (!(Plugin.getInstance().getPluginMode() instanceof Minigame)) {
            commandSender.sendMessage(Lang.get("mapVoting.invalidMode"));
            return true;
        }
        Game game =
                ((Minigame) Plugin.getInstance().getPluginMode()).getGame();
        if (!game.getState().equals(Game.State.WAITING)) {
            commandSender.sendMessage(Lang.get("mapVoting.invalidState"));
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage(Lang.get("mapVoting.noMapName"));
            return true;
        }
        String mapName = args[0];
        game.getLobby().getMapVoting().vote((Player) commandSender, mapName);
        if (args.length > 1 && args[1].equals("admin")) {
            if (!commandSender.isOp()) {
                commandSender.sendMessage("§4Опа у тебя нет опА");
                return true;
            }
            game.getLobby().getMapVoting().getMap(mapName).setVoteCount(228);
        }
        return true;
    }
}
