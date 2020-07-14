package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.MagnetizeOrbInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MagnetizeOrb extends Item implements ProjectileHitInterface,
        InfiniteReplenishInterface, PlayerInteractInterface {
    private final double radius;
    public MagnetizeOrb(final String name, final int level, final Player owner) {
        super(name, level, owner);
        radius = ((MagnetizeOrbInfo) getInfo()).getRadius();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity proj = event.getEntity();
        final Location loc = proj.getLocation();
        for (final Entity obj : GameUtils.getNearby(proj.getLocation(), radius)) {
            if (GameUtils.isEnemy(obj, getTeam())) {
                final Vector add = loc.toVector().subtract(obj.getLocation().toVector());
                final double force = Math.log(add.length()) / Math.log(2);
                obj.setVelocity(obj.getVelocity().add(add.normalize().multiply(force)));
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Projectile proj = GameUtils.spawnProjectile(owner.getEyeLocation(),
                1.2, EntityType.SNOWBALL, owner);
        summonedEntityIds.add(proj.getUniqueId());
    }
}