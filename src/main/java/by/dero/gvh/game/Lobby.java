package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.Board;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.utils.Board.getNormal;
import static by.dero.gvh.utils.Board.sendTitle;

public class Lobby {
    private final Game game;
    private final Board board;
    private int timeLeft = 60;
    private int showIndex = 0;

    public Lobby(Game game) {
        this.game = game;
        board = new Board("Lobby", 2);
        Bukkit.getServer().getScheduler().runTaskTimer(Plugin.getInstance(), ()->
                board.update(new String[] {
                                "§aPreparing: §4 %d/%d",
                                "§bTime left : %d"
                        },
                        new int[] {
                                game.getPlayers().size(),
                                game.getInfo().getMinPlayerCount(),
                                timeLeft
                        }), 0, 10);
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    public void startGame() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ready) {
                    timeLeft = 60;
                    showIndex = 0;
                    this.cancel();
                    return;
                }
                if (showTime[showIndex] == timeLeft) {
                    sendTitle(ChatColor.GREEN + "" + timeLeft, game.getPlayers().values());
                    showIndex++;
                }
                timeLeft--;

                if (timeLeft == 0) {
                    sendTitle(ChatColor.GREEN + "Game Started", game.getPlayers().values());
                    game.start();
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size();
        final int needed = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage(getNormal("§aPlayer " +
                gamePlayer.getPlayer().getName() + " joined! " + players + '/' + needed));
        gamePlayer.getPlayer().setScoreboard(board.getScoreboard());
        if (players >= needed && !ready) {
            ready = true;
            this.startGame();
        }
        if (players >= game.getInfo().getMaxPlayerCount()) {
            timeLeft = 10;
        }
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int need = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage(getNormal("§aPlayer " +
                gamePlayer.getPlayer().getName() + " left! " + players + '/' + need));

        if (players < need) {
            ready = false;
        }
    }

    public Game getGame () {
        return game;
    }
}