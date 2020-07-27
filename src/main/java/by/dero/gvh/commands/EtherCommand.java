package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EtherCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage("§4Опа у тебя нет опА");
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage("§4Инструкции няма, добро пожаловать в EtherCommand.java");
            return true;
        }
        if (args[0].equalsIgnoreCase("analyzeall")) {
            int startID = 0;
            if (args.length > 1) {
                startID = Integer.parseInt(args[1]);
            }
            Plugin.getInstance().getGameStatsData().analyzeAllAndSave(startID);
            commandSender.sendMessage("§aАнализ прошел успешно, искать в mongodb::analyzeReports");
            return true;
        } else if (args[0].equalsIgnoreCase("addbooster")) {
            if (args.length != 3) {
                commandSender.sendMessage("§4/ether addbooster <player> <booster>");
                return true;
            }
            PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(args[1]);
            info.activateBooster(args[2]);
            Plugin.getInstance().getPlayerData().savePlayerInfo(info);
            commandSender.sendMessage("§aБустер выдан");
            return true;
        } if (args[0].equalsIgnoreCase("addexp")) {
            if (args.length != 3) {
                commandSender.sendMessage("§4/ether addexp <player> <count>");
                return true;
            }
            int count = Integer.parseInt(args[2]);
            PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(args[1]);
            info.setBalance(info.getBalance() + count);
            Plugin.getInstance().getPlayerData().savePlayerInfo(info);
            if (Plugin.getInstance().getPluginMode() instanceof Lobby) {
                if (Lobby.getInstance().getPlayers().containsKey(args[1])) {
                    LobbyPlayer lp = Lobby.getInstance().getPlayers().get(args[1]);
                    lp.setPlayerInfo(info);
                    Lobby.getInstance().updateDisplays(lp.getPlayer());
                }
            }
            commandSender.sendMessage("§aОпыт выдан");
            return true;
        }
        return true;
    }
}
