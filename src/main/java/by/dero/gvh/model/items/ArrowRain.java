package by.dero.gvh.model.items;

import by.dero.gvh.Cooldown;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.ArrowRainInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static by.dero.gvh.utils.DataUtils.isEnemy;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class ArrowRain extends Item implements UltimateInterface, Listener {
    private final double radius;
    private final int arrowCycles;
    private final int cycleDelay;
    private final double height = 10;
    private final HashSet<UUID> arrows = new HashSet<>();
    private final Cooldown cooldown;

    public ArrowRain(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final ArrowRainInfo info = (ArrowRainInfo) getInfo();
        radius = info.getRadius();
        arrowCycles = info.getArrowCycles();
        cycleDelay = info.getCycleDelay();
        cooldown = new Cooldown(info.getCooldown());
        cooldown.makeReady();
    }

    @Override
    public void drawSign(final Location loc) {
        for (int rad = 10; rad <= radius; rad += 10)
            Drawings.drawCircle(loc.clone().add(0, height, 0), rad, Particle.EXPLOSION_LARGE);
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            sendCooldownMessage(player, getInfo().getDisplayName(), cooldown.getSecondsRemaining());
            return;
        }
        cooldown.reload();
        new BukkitRunnable() {
            int times = 0;
            final Location center = player.getLocation().clone().add(0, height, 0);
            @Override
            public void run() {
                double dst = Math.random() * radius, angle = Math.random() * Math.PI * 2;
                Location shooter = center.clone().add(dst*Math.cos(angle),0,dst*Math.sin(angle));
                List<Location> targets = new ArrayList<>();
                for (Entity obj : Objects.requireNonNull(center.getWorld()).getNearbyEntities(center, radius, 50, radius)) {
                    if (isEnemy(obj, team)) {
                        targets.add(obj.getLocation().clone());
                    }
                }
                for (Location obj : targets) {
                    Arrow arrow = center.getWorld().spawnArrow(shooter,
                            obj.toVector().subtract(shooter.toVector()).normalize(),
                            4, 1);
                    arrows.add(arrow.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!arrows.contains(arrow.getUniqueId())) {
                                this.cancel();
                            }
                            arrow.getWorld().spawnParticle(Particle.LAVA, arrow.getLocation(), 1);
                        }
                    }.runTaskTimer(Plugin.getInstance(), 0, 2);
                }
                targets.clear();

                if (++times > arrowCycles) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Plugin.getInstance(), 0, cycleDelay);
        new BukkitRunnable() {
            double times = 0;
            final Location center = player.getLocation().clone();
            @Override
            public void run() {
                drawSign(center);
                if (++times > arrowCycles) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, cycleDelay);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        arrows.remove(event.getEntity().getUniqueId());
    }
}
