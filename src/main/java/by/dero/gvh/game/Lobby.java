package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby {
    private final Game game;

    public Lobby(Game game) {
        this.game = game;
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
                }
                if (showTime[showIndex] == timeLeft) {
                    PacketPlayOutTitle title = new PacketPlayOutTitle(
                            PacketPlayOutTitle.EnumTitleAction.TITLE,
                            ChatSerializer.a("{\"text\":\"" + (ChatColor.GREEN + "" + timeLeft) + "\"}"),
                            0, 20, 0);
                    for (GamePlayer player : game.getPlayers().values()) {
                        ((CraftPlayer)player.getPlayer()).getHandle().playerConnection.sendPacket(title);
                    }
                    showIndex++;
                }
                timeLeft--;
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
        if (players >= needed) {
            ready = true;
            startGame();
        }
        Plugin.getInstance().getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " joined! " + players + '/' + needed);
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int need = game.getInfo().getMinPlayerCount();
        Plugin.getInstance().getServer().broadcastMessage("§aPlayer " +
                gamePlayer.getPlayer().getName() + " left! " + players + '/' + need);
        if (players < need) {
            ready = false;
        }
    }

    public Game getGame() {
        return game;
    }
}
