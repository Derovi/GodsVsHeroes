package by.dero.gvh.utils;

import by.dero.gvh.GameObject;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Invisibility {
    public static void makeInvisible(final GameObject entity, final int time) {
        final Game game = Minigame.getInstance().getGame();
        entity.addEffect(new PotionEffect(PotionEffectType.INVISIBILITY, time, 0));
        if (entity instanceof GamePlayer) {
            GamePlayer player = (GamePlayer) entity;
            player.getPlayer().getWorld().getPlayers();
            for (final GamePlayer gp : game.getPlayers().values()) {
                if (player.getTeam() != gp.getTeam()) {
                    gp.getPlayer().hidePlayer(Plugin.getInstance(), player.getPlayer());
                }
            }
            final BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                        this.cancel();
                        return;
                    }
                    for (final GamePlayer gp : game.getPlayers().values()) {
                        if (player.getTeam() != gp.getTeam()) {
                            gp.getPlayer().showPlayer(Plugin.getInstance(), player.getPlayer());
                        }
                    }
                }
            };
            runnable.runTaskLater(Plugin.getInstance(), time);
            game.getRunnables().add(runnable);
        }
    }
}
