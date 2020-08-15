package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.GameStatsData;
import by.dero.gvh.stats.PlayerStats;
import com.google.gson.Gson;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

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
            commandSender.sendMessage("§aОпыт выдан");
            return true;
        } if (args[0].equalsIgnoreCase("printtop")) {
            StringBuilder summary = new StringBuilder();
            int idx = 1;
            for (Document document : Plugin.getInstance().getGameStatsData().getPlayersCollection().aggregate(
                    Collections.singletonList(new BsonDocument("$sort", new BsonDocument("exp", new BsonInt32(-1)))))) {
                PlayerStats stats = new Gson().fromJson(document.toJson(), PlayerStats.class);
                summary.append(idx).append(") ").append(stats.getName()).append(" - ").append(stats.getExp()).append(" (")
                        .append(stats.getWins()).append(" побед, ").append(stats.getLooses()).append(" поражений)").append('\n');
                ++idx;
                if (idx > 1000) {
                    break;
                }
            }
            try {
                new LocalStorage().save("summary", "stats", summary.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            commandSender.sendMessage("§aПосчитано");
            return true;
        }
        return true;
    }
}
