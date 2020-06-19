package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.Board;
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
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    public void updateDisplays() {
        board.update(new String[] {
                Lang.get("gameLobby.boardReady").
                        replace("%cur%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())).
                        replace("%max%", String.valueOf(game.getInfo().getMaxPlayerCount())),
                Lang.get("gameLobby.boardRequired").
                        replace("%min%", String.valueOf(game.getInfo().getMinPlayerCount())),
                Lang.get("gameLobby.boardTimeLeft").
                        replace("%time%", String.valueOf(timeLeft))
        });
    }

    public void startGame() {
        timeLeft = 60;
        showIndex = 0;
        ready = false;
        sendTitle(Lang.get("game.gameAlreadyStarted"), game.getPlayers().values());
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
                updateDisplays();
                if (showTime[showIndex] == timeLeft) {
                    sendTitle("§a" + timeLeft, game.getPlayers().values());
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
        final int needed = game.getInfo().getMaxPlayerCount();
        Bukkit.getServer().broadcastMessage(Lang.get("gameLobby.playerJoined")
                .replace("%name%", gamePlayer.getPlayer().getName())
                .replace("%cur%", String.valueOf(players))
                .replace("%max%", String.valueOf(needed))
        );
        gamePlayer.getPlayer().setScoreboard(board.getScoreboard());
        updateDisplays();
        gamePlayer.getPlayer().getInventory().clear();
        if (players >= game.getInfo().getMinPlayerCount() && !ready) {
            ready = true;
            startPrepairing();
        }
        if (players >= game.getInfo().getMaxPlayerCount()) {
            timeLeft = 10;
        }
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int needed = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage(Lang.get("gameLobby.playerLeft")
                .replace("%name%", gamePlayer.getPlayer().getName())
                .replace("%cur%", String.valueOf(players))
                .replace("%max%", String.valueOf(needed))
        );

        updateDisplays();
        if (players < needed) {
            ready = false;
        }
    }

    public Game getGame () {
        return game;
    }
}