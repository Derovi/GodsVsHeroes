package by.dero.gvh.model.items;

import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.DragonBreathInfo;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;

public class DragonBreath extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final double radius;
    private final double damage;
    private final double spread = Math.toRadians(20);
    private final double cosSpread = MathUtils.cos(spread)*0.9;

    public DragonBreath(String name, int level, Player owner) {
        super(name, level, owner);
        DragonBreathInfo info = (DragonBreathInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Location loc = event.getPlayer().getEyeLocation().clone();
        final Vector dlt = event.getPlayer().getLocation().toVector().subtract(
                Minigame.getInstance().getGameEvents().getLastPos().get(owner.getUniqueId())).multiply(3);
        for (int i = 0; i < 20; i++) {
            Vector at = loc.getDirection().clone();
            double x = Math.random() * spread * 2 - spread;
            double y = Math.random() * spread * 2 - spread;
            at = MathUtils.rotateAroundAxis(at, MathUtils.getRightVector(at), x);
            at = MathUtils.rotateAroundAxis(at, new Vector(0,1,0), y);
            loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(dlt), 0,
                    at.getX() / 20, at.getY() / 20, at.getZ() / 20, 7);
        }
        for (final LivingEntity entity : getNearby(loc, radius)) {
            final Vector a = entity.getLocation().toVector().clone().subtract(loc.toVector()).normalize();
            final Vector b = loc.getDirection().clone();
            b.setY(0);
            a.setY(0);
            if (a.dot(b) / a.length() / b.length() >= cosSpread && isEnemy(entity, getTeam())) {
                entity.setFireTicks(100);
                DataUtils.damage(damage, entity, owner);
            }
        }
    }
}
