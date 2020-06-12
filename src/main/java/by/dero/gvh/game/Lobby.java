package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class Lobby {
    private final Game game;
    private final Scoreboard board;
    private final Team counterTeam;
    private final Team timeLeftTeam;

    public Lobby(Game game) {
        this.game = game;

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ServerName", "dummy", "Lobby");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        counterTeam = board.registerNewTeam("counterTeam");
        counterTeam.setSuffix("");
        counterTeam.addEntry("§a");
        obj.getScore("§a").setScore(2);

        timeLeftTeam = board.registerNewTeam("timeLeftTeam");
        timeLeftTeam.setSuffix("");
        timeLeftTeam.setPrefix("§eWaiting for players");
        timeLeftTeam.addEntry("§b");
        obj.getScore("§b").setScore(1);
    }

    private void updatePlayerBoard() {
        counterTeam.setPrefix("§aPreparing: §4" +
                game.getPlayers().size() + "/" + game.getInfo().getMinPlayerCount());
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    public void startGame() {
        new BukkitRunnable() {
            int timeLeft = 60, showIndex = 0;
            @Override
            public void run() {
                if (!ready) {
                    timeLeftTeam.setPrefix("§eWaiting for players");
                    this.cancel();
                }
                if (showTime[showIndex] == timeLeft) {
                    PacketPlayOutTitle title = new PacketPlayOutTitle(
                            PacketPlayOutTitle.EnumTitleAction.TITLE,
                            ChatSerializer.a("{\"text\":\"" + (ChatColor.GREEN + "" + timeLeft) + "\"}"),
                            0, 20, 0);
                    for (GamePlayer player : game.getPlayers().values()) {
                        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
                    }
                    showIndex++;
                }
                timeLeft--;
                timeLeftTeam.setPrefix("§bTime left : " + timeLeft);

                if (timeLeft == 0) {
                    game.start();
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        final int needed = game.getInfo().getMinPlayerCount();
        final int players = game.getPlayers().size();
        Bukkit.getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " joined! " + players + '/' + needed);
        updatePlayerBoard();
        gamePlayer.getPlayer().setScoreboard(board);
        if (players >= needed) {
            ready = true;
            this.startGame();
        }
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int need = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " left! " + players + '/' + need);
        updatePlayerBoard();
        if (players < need) {
            ready = false;
        }
    }

    public Game getGame () {
        return game;
    }
}