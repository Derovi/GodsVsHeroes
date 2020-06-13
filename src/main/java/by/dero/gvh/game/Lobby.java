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
        counterTeam.addEntry("§a".substring(1));
        obj.getScore("§a".substring(1)).setScore(2);

        timeLeftTeam = board.registerNewTeam("timeLeftTeam");
        timeLeftTeam.setSuffix("");
        updateWaiting();
        timeLeftTeam.addEntry("§b".substring(1));
        obj.getScore("§b".substring(1)).setScore(1);
    }

    private void updatePlayerBoard() {
        String kek = getNormal("§aPreparing: §4" +
                game.getPlayers().size() + "/" + game.getInfo().getMinPlayerCount());
        counterTeam.setPrefix(kek);
    }
    private void updateWaiting() {
        String kek = getNormal("§eWaiting for players");
        timeLeftTeam.setPrefix(kek);
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};

    public void startGame() {
        new BukkitRunnable() {
            int timeLeft = 60, showIndex = 0;
            @Override
            public void run() {
                if (!ready) {
                    this.cancel();
                    return;
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
                timeLeftTeam.setPrefix("§bTime left : ".substring(1) + timeLeft);

                if (timeLeft == 0) {
                    PacketPlayOutTitle title = new PacketPlayOutTitle(
                            PacketPlayOutTitle.EnumTitleAction.TITLE,
                            ChatSerializer.a("{\"text\":\"" + (ChatColor.GREEN + "Game Started") + "\"}"),
                            0, 20, 0);
                    for (GamePlayer player : game.getPlayers().values()) {
                        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(title);
                    }
                    game.start();
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public static String getNormal(String msg) {
        int pos = msg.indexOf("§");
        if (pos != -1) {
            msg = msg.replace(msg.substring(pos, pos+1), "");
        }
        return msg;
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        final int needed = game.getInfo().getMinPlayerCount();
        final int players = game.getPlayers().size();
        Bukkit.getServer().broadcastMessage(getNormal("§aPlayer " +
                gamePlayer.getPlayer().getName() + " joined! " + players + '/' + needed));
        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () ->
        { updatePlayerBoard(); updateWaiting();}, 5);
        gamePlayer.getPlayer().setScoreboard(board);
        if (players >= needed) {
            ready = true;
            this.startGame();
        }
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int need = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage(getNormal("§aPlayer " +
                gamePlayer.getPlayer().getName() + " left! " + players + '/' + need));
        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () ->
        { updatePlayerBoard(); updateWaiting();}, 5);

        if (players < need) {
            ready = false;
        }
    }

    public Game getGame () {
        return game;
    }
}