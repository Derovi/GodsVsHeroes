package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.ArrowRainInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ArrowRain extends Item implements UltimateInterface {
    private final double radius;
    private final int arrowCycles;
    private final int cycleDelay;
    private final double height = 10;
    private final HashSet<Arrow> arrows = new HashSet<>();

    public ArrowRain(String name, int level, Player owner) {
        super(name, level, owner);
        ArrowRainInfo info = (ArrowRainInfo)getInfo();
        radius = info.getRadius();
        arrowCycles = info.getArrowCycles();
        cycleDelay = info.getCycleDelay();
    }

    @Override
    public void drawSign(Location loc) {
        for (int rad = 10; rad <= radius; rad += 10)
            Drawings.drawCircle(loc.clone().add(0, height, 0), rad, Particle.EXPLOSION_LARGE);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            int times = 0;
            final Location center = player.getLocation().clone().add(0, height, 0);
            @Override
            public void run() {
                double dst = Math.random() * radius, angle = Math.random() * Math.PI * 2;
                Location shooter = center.clone().add(dst*Math.cos(angle),0,dst*Math.sin(angle));
                List<Location> targets = new ArrayList<>();
                for (Entity obj : Objects.requireNonNull(center.getWorld()).getNearbyEntities(center, radius, 50, radius)) {
                    if (!(obj instanceof LivingEntity) || obj.isDead() || obj == player) {
                        continue;
                    }
                    targets.add(obj.getLocation().clone());
                }
                for (Location obj : targets) {
                    Arrow arrow = center.getWorld().spawnArrow(shooter,
                            obj.toVector().subtract(shooter.toVector()).normalize(),
                            4, 1);
                    arrows.add(arrow);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!arrows.contains(arrow)) {
                                this.cancel();
                            }
                            arrow.getWorld().spawnParticle(Particle.LAVA, arrow.getLocation(), 1);
                        }
                    }.runTaskTimer(Plugin.getInstance(), 0, 1);
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
}