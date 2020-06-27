package by.dero.gvh.model.items;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.FireSpearInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

import static by.dero.gvh.model.Drawings.drawCircleInFront;
import static by.dero.gvh.model.Drawings.drawLine;
import static by.dero.gvh.utils.DataUtils.damage;
import static by.dero.gvh.utils.DataUtils.isEnemy;

public class FireSpear extends Item implements PlayerInteractInterface {
    private final int parts = 6;
    private final double speed = 5;
    private final int time = 140;
    private final double damage;
    public FireSpear(final String name, final int level, final Player owner) {
        super(name, level, owner);
        damage = ((FireSpearInfo) getInfo()).getDamage();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();

        final Location loc = event.getPlayer().getEyeLocation().clone();
        loc.add(loc.getDirection().multiply(2));
        final Vector dlt = loc.getDirection().multiply(0.05 * speed);
        final HashSet<Player> left = new HashSet<>();
        for (final GamePlayer gp : Game.getInstance().getPlayers().values()) {
            if (isEnemy(gp.getPlayer(), team)) {
                left.add(gp.getPlayer());
            }
        }
        final BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = 0;
            final ArrayList<Player> rem = new ArrayList<>();
            @Override
            public void run() {
                rem.clear();
                for (final Player p : left) {
                    if (p.getLocation().distance(loc) < 3 || p.getEyeLocation().distance(loc) < 3) {
                        rem.add(p);
                        damage(damage, p, owner);
                    }
                }
                left.removeAll(rem);
                rem.clear();

                for (int i = 0; i < 6; i++) {
                    drawCircleInFront(loc, i * 0.5, -i * 1.5, parts, Particle.FLAME);
                }
                drawLine(loc, loc.clone().subtract(dlt.clone().multiply(Math.min(30, ticks))), Particle.END_ROD);
                loc.add(dlt);
                if (++ticks >= time) {
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);

    }
}
