package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {
    static boolean flag = false;
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        
        if (args.length == 0) {
            flag = true;
            new BukkitRunnable() {
                int idx = 0;
                @Override
                public void run() {
                    if (idx > 400 || !flag) {
                        this.cancel();
                    } else {
                        player.sendMessage(Sound.values()[idx].toString());
                        player.playSound(player.getEyeLocation(), Sound.values()[idx], 1, 1);
                    }
                    idx++;
                }
            }.runTaskTimer(Plugin.getInstance(), 20, 20);
        } else {
            try {
                flag = false;
                player.playSound(player.getEyeLocation(), Sound.values()[Integer.parseInt(args[0])], 1, 1);
                player.sendMessage(Sound.values()[Integer.parseInt(args[0])].toString());
            } catch (Exception e) {
        
            }
        }
        
//        player.sendMessage(GameStatsUtils.getDateString(System.currentTimeMillis()));
//        PlayerStats playerStats = Plugin.getInstance().getGameStatsData().getPlayerStats(player.getName());
//        if (playerStats.getGames().isEmpty()) {
//            player.sendMessage("§4No games!");
//            return true;
//        }
//        int id = playerStats.getGames().get(playerStats.getGames().size() - 1);
//        GameStats gameStats = Plugin.getInstance().getGameStatsData().getGameStats(id);
//        System.out.println(gameStats == null);
//        GameStatsBook gameStatsBook = new GameStatsBook(Plugin.getInstance().getBookManager(),
//                player, player, gameStats);
//        gameStatsBook.build();
//        gameStatsBook.open();
        return true;
    }
}
