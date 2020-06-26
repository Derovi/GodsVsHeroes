package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.DragonBreathInfo;
import by.dero.gvh.utils.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static by.dero.gvh.model.Drawings.getRightVector;
import static by.dero.gvh.model.Drawings.rotateAroundAxis;
import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;

public class DragonBreath extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final double radius;
    private final double damage;
    private final double spread = Math.toRadians(20);
    private final double cosSpread = Math.cos(spread);

    public DragonBreath(String name, int level, Player owner) {
        super(name, level, owner);
        DragonBreathInfo info = (DragonBreathInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final Location loc = event.getPlayer().getEyeLocation().clone();
        for (int i = 0; i < 20; i++) {
            Vector at = loc.getDirection().clone();
            double x = Math.random() * spread * 2 - spread;
            double y = Math.random() * spread * 2 - spread;
            at = rotateAroundAxis(at, getRightVector(at), x);
            at = rotateAroundAxis(at, new Vector(0,1,0), y);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 0,
                    at.getX()*radius / 20, at.getY()*radius / 20, at.getZ()*radius / 20);
        }
        for (final LivingEntity entity : getNearby(loc, radius)) {
            final Vector a = entity.getLocation().toVector().clone().subtract(loc.toVector()).normalize();
            a.setY(0);
            final Vector b = loc.getDirection();
            if (a.dot(b) / a.length() / b.length() >= cosSpread && isEnemy(entity, team)) {
                entity.setFireTicks(100);
                DataUtils.damage(damage, entity, owner);
            }
        }
    }
}
