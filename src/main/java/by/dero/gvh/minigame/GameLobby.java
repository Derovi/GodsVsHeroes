package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import static by.dero.gvh.utils.Board.sendTitle;

public class GameLobby {
    private final Game game;
    private final Board board;
    private int timeLeft = 60;
    private int showIndex = 0;

    public GameLobby(Game game) {
        this.game = game;
        board = new Board("Lobby", 3);
        Bukkit.getServer().getScheduler().runTaskTimer(Plugin.getInstance(), ()->
                board.update(new String[] {
                                "§aPreparing: §4 " + Bukkit.getServer().getOnlinePlayers().size() + "/" + game.getInfo().getMaxPlayerCount(),
                                "§aMinimum required: " + game.getInfo().getMinPlayerCount(),
                                "§bTime left : " + timeLeft
                        }), 0, 20);
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    public void startGame() {
        timeLeft = 60;
        showIndex = 0;
        ready = false;
        sendTitle(ChatColor.GREEN + "Game Started", game.getPlayers().values());
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("showhealth", "health", "");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");

        for(Player online : Bukkit.getOnlinePlayers()){
            online.setScoreboard(board);
            online.setHealth(online.getHealth());
        }
        game.start();
    }

    public void startPrepairing() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ready) {
                    showIndex = 0;
                    timeLeft = 60;
                    this.cancel();
                    return;
                }
                if (showTime[showIndex] == timeLeft) {
                    sendTitle(ChatColor.GREEN + "" + timeLeft, game.getPlayers().values());
                    showIndex++;
                }
                timeLeft--;

                if (timeLeft == 0) {
                    startGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size();
        final int needed = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " joined! " + players + '/' + needed);
        gamePlayer.getPlayer().setScoreboard(board.getScoreboard());
        gamePlayer.getPlayer().getInventory().clear();
        if (players >= needed && !ready) {
            ready = true;
            startPrepairing();
        }
        if (players >= game.getInfo().getMaxPlayerCount()) {
            timeLeft = 10;
        }
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int need = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " left! " + players + '/' + need);

        if (players < need) {
            ready = false;
        }
    }

    public Game getGame () {
        return game;
    }
}