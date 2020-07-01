package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SuicideJumpInfo;
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.GameUtils.*;

public class SuicideJump extends Item implements PlayerInteractInterface {
    private final double radius;
    private final double selfDamage;
    private final double damage;

    public SuicideJump(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final SuicideJumpInfo info = (SuicideJumpInfo) getInfo();
        radius = info.getRadius();
        selfDamage = info.getSelfDamage();
        damage = info.getSelfDamage();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        GravityFireball gravityFireball = new GravityFireball(player.getLocation().clone().add(0, -1,0));
        gravityFireball.addPassenger(player);
        gravityFireball.setVelocity(player.getLocation().getDirection().normalize().multiply(1.3));
        gravityFireball.spawn();

        gravityFireball.setOnHit(() -> {
            final Location loc = player.getLocation();
            damage(selfDamage, player, player);
            for (final LivingEntity entity : getNearby(loc, radius)) {
                if (isEnemy(entity, getTeam())) {
                    damage(damage, entity, player);
                }
            }
            for (int i = 0; i < 5; i++) {
                final Location at = MathUtils.randomCylinder(loc,radius - 1, -2);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 0, 0, 0, 0);
            }
            for (int i = 0; i < 20; i++) {
                final Location at = MathUtils.randomCylinder(loc, radius, -2);
                Drawings.drawLine(at, at.clone().add(0, 3, 0), Particle.SMOKE_LARGE);
            }
        });
    }
}
