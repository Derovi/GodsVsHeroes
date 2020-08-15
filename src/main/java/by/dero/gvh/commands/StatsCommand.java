package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.books.PlayerStatsBook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§6Пример: §f/stats <игрок>");
            return true;
        }
        String target = args[0];
        if (!Plugin.getInstance().getPlayerData().isPlayerRegistered(target)) {
            commandSender.sendMessage("§cИгрок не найден!");
            return true;
        }
        PlayerStatsBook playerStatsBook = new PlayerStatsBook(Plugin.getInstance().getBookManager(),
                (Player) commandSender, target);
        playerStatsBook.build();
        playerStatsBook.open();
        return true;
    }
}
