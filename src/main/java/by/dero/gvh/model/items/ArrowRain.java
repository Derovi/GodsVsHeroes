package by.dero.gvh.model.items;

import by.dero.gvh.Cooldown;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.ArrowRainInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static by.dero.gvh.model.Drawings.getInCircle;
import static by.dero.gvh.model.Drawings.randomCylinder;
import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class ArrowRain extends Item implements UltimateInterface, Listener {
    private final double radius;
    private final int arrowCycles;
    private final int cycleDelay;
    private final double height = 10;

    public ArrowRain(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final ArrowRainInfo info = (ArrowRainInfo) getInfo();
        radius = info.getRadius();
        arrowCycles = info.getArrowCycles();
        cycleDelay = info.getCycleDelay();
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
            if (System.currentTimeMillis() - cooldown.getStartTime() > 100) {
                sendCooldownMessage(player, getInfo().getDisplayName(), cooldown.getSecondsRemaining());
            }
            return;
        }
        cooldown.reload();
        final Location center = player.getLocation().clone().add(0, height, 0);
        new BukkitRunnable() {
            int times = 0;
            @Override
            public void run() {
                final Location shooter = randomCylinder(center, radius, 0);
                Bukkit.getServer().broadcastMessage(shooter.toString());
                final List<Location> targets = new ArrayList<>();
                for (LivingEntity obj : getNearby(center, radius)) {
                    if (isEnemy(obj, team)) {
                        targets.add(obj.getLocation().clone());
                    }
                }
                for (Location obj : targets) {
                    Arrow arrow = center.getWorld().spawnArrow(shooter,
                            obj.toVector().subtract(shooter.toVector()).normalize(),
                            5, 1);
                    arrow.setShooter(getOwner());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (arrow.isValid()) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Plugin.getInstance(), 0, 1);
                }
                targets.clear();

                if (++times > arrowCycles) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Plugin.getInstance(), 0, cycleDelay);
        final int particleNumber = 5;
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                for (int i = 0; i < particleNumber; i++) {
                    center.getWorld().spawnParticle(Particle.FLASH, randomCylinder(center, radius, height), 1);
                    center.getWorld().spawnParticle(Particle.LAVA, randomCylinder(center, radius, height), 1);
                }
                if (++ticks >= cycleDelay * arrowCycles) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
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
}
