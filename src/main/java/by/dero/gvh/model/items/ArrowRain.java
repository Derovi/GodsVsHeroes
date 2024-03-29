package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ArrowRainInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ArrowRain extends Item implements PlayerInteractInterface {
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

    public void drawSign(final Location loc) {
        for (int rad = 10; rad <= radius; rad += 10)
            Drawings.drawCircle(loc.clone().add(0, height, 0), rad, Particle.EXPLOSION_LARGE);
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
        final Location center = owner.getLocation().clone().add(0, height, 0);
        new BukkitRunnable() {
            int times = 0;
            @Override
            public void run() {
                final Location shooter = MathUtils.randomCylinder(center, radius, 0);
                owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1.07f, 1);
                final List<Location> targets = new ArrayList<>();
                for (LivingEntity obj : GameUtils.getNearby(center, radius)) {
                    if (GameUtils.isEnemy(obj, getTeam())) {
                        targets.add(obj.getLocation().clone());
                    }
                }
                for (Location obj : targets) {
                    Arrow arrow = center.getWorld().spawnArrow(shooter,
                            obj.toVector().subtract(shooter.toVector()).normalize(),
                            4F, 1);
                    arrow.setShooter(owner);
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
                    center.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, MathUtils.randomCylinder(center, radius, height), 1);
                    center.getWorld().spawnParticle(Particle.LAVA, MathUtils.randomCylinder(center, radius, height), 1);
                }
                if (++ticks >= cycleDelay * arrowCycles) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
        new BukkitRunnable() {
            double times = 0;
            final Location center = owner.getLocation().clone();
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
