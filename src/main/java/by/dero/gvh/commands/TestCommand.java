package by.dero.gvh.commands;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.monuments.BoosterStand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
    
        for (BoosterStand stand : Lobby.getInstance().getMonumentManager().getBoosters()) {
            stand.setAnimCnt(stand.getAnimCnt() + 1);
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
