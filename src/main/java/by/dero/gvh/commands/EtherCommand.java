package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
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
            Plugin.getInstance().getGameStatsData().analyzeAllAndSave();
            commandSender.sendMessage("§aАнализ прошел успешно, искать в mongodb::analyzeReports");
            return true;
        }
        return true;
    }
}
