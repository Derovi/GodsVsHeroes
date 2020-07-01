package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.utils.MessagingUtils.sendTitle;
import static org.apache.commons.lang.ArrayUtils.indexOf;

public class GameLobby {
    private final Game game;
    private int timeLeft = 60;
    private BukkitRunnable prepairing;

    public GameLobby(Game game) {
        this.game = game;
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    private void updateDisplays() {
        for (final GamePlayer gp : game.getPlayers().values()) {
            gp.getBoard().update(
                    new String[] {
                            Lang.get("gameLobby.boardReady").
                                    replace("%cur%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())).
                                    replace("%max%", String.valueOf(game.getInfo().getMaxPlayerCount())),
                            Lang.get("gameLobby.boardRequired").
                                    replace("%min%", String.valueOf(game.getInfo().getMinPlayerCount())),
                            Lang.get("gameLobby.boardTimeLeft").
                                    replace("%time%", String.valueOf(timeLeft))
                    }
            );
        }
    }

    public void startGame() {
        timeLeft = 60;
        if (prepairing != null) {
            prepairing.cancel();
        }
        ready = false;
        sendTitle(Lang.get("game.gameAlreadyStarted"), game.getPlayers().values());

        for(Player online : Bukkit.getOnlinePlayers()){
            online.setHealth(online.getHealth());
        }
        game.start();
    }

    public void startPrepairing() {
        prepairing = new BukkitRunnable() {
            @Override
            public void run() {
                if (!ready) {
                    this.cancel();
                    timeLeft = 60;
                    updateDisplays();
                    return;
                }
                if (timeLeft <= 10) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(timeLeft);
                        player.setHealth(timeLeft);
                    }
                }
                updateDisplays();
                if (indexOf(showTime, timeLeft) != -1) {
                    sendTitle("§a" + timeLeft, game.getPlayers().values());
                }
                timeLeft--;

                if (timeLeft == 0) {
                    this.cancel();
                    startGame();
                }
            }
        };
        prepairing.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        gamePlayer.setBoard(new Board("Lobby", 3));

        final int players = game.getPlayers().size();
        final int needed = game.getInfo().getMaxPlayerCount();

        Bukkit.getServer().broadcastMessage(Lang.get("gameLobby.playerJoined")
                .replace("%name%", gamePlayer.getPlayer().getName())
                .replace("%cur%", String.valueOf(players))
                .replace("%max%", String.valueOf(needed))
        );
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
                .replace("%max%", String.valueOf(game.getInfo().getMaxPlayerCount()))
        );

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), ()-> {
            updateDisplays();
            if (players < needed) {
                ready = false;
            }
        }, 2);
    }

    public Game getGame () {
        return game;
    }
}