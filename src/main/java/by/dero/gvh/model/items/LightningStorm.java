package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.LightningStormInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static by.dero.gvh.model.Drawings.drawLine;
import static by.dero.gvh.model.Drawings.getInCircle;
import static by.dero.gvh.utils.DataUtils.*;

public class LightningStorm extends Item implements UltimateInterface {
    private final double radius;
    private final int strikes;
    private final double[] signRadius;
    private final long delayStrikes;
    private final Particle drawParticle;

    public LightningStorm(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final LightningStormInfo info = (LightningStormInfo) getInfo();
        radius = info.getRadius();
        strikes = info.getStrikes();
        signRadius = info.getSignRadius();
        delayStrikes = info.getDelayStrikes();
        drawParticle = info.getDrawParticle();
    }

    @Override
    public void drawSign(final Location loc) {
        new BukkitRunnable() {
            long passed;
            @Override
            public void run() {
                final Location cur = loc.clone();
                double startAngle = 0;
                for (int st = 0; st < 2; st ++) {
                    cur.add(0,4,0);
                    Drawings.drawCircle(cur, signRadius[st], drawParticle);
                    for (double angle = startAngle; angle < Math.PI * 2; angle += Math.PI / 2.5) {
                        drawLine(getInCircle(cur, signRadius[st], angle),
                                getInCircle(cur, signRadius[st], angle + Math.PI * 0.8),
                                drawParticle);
                    }
                    startAngle += Math.PI / 5;
                }
                passed += 5;
                if (passed >= delayStrikes * strikes) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 5);
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        drawSign(player.getLocation().clone());

        new BukkitRunnable() {
            int times = 0;
            final Location center = player.getLocation().clone();
            @Override
            public void run() {
                for (final LivingEntity obj : getNearby(center, radius)) {
                    if (isAlly(obj, getTeam()) && center.distance(obj.getLocation()) <= radius) {
                        setLastUsedLightning(getOwner());
                        center.getWorld().strikeLightning(obj.getLocation());
                    }
                }
                times++;
                if (times == strikes) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, delayStrikes);
    }
}
