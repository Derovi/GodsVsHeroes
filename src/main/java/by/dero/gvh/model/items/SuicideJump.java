package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SuicideJumpInfo;
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuicideJump extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
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
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        GravityFireball gravityFireball = new GravityFireball(owner.getLocation().clone().add(0, -1,0));
        gravityFireball.addPassenger(owner);
        gravityFireball.setVelocity(owner.getLocation().getDirection().normalize().multiply(1.2).add(MathUtils.UPVECTOR));
        gravityFireball.spawn();

        gravityFireball.setOnHit(() -> {
            final Location loc = owner.getLocation();
            GameUtils.damage(selfDamage, owner, owner);
            for (final LivingEntity entity : GameUtils.getNearby(loc, radius)) {
                if (GameUtils.isEnemy(entity, getTeam())) {
                    GameUtils.damage(damage, entity, owner);
                }
            }
            for (int i = 0; i < 5; i++) {
                final Location at = MathUtils.randomCylinder(loc,radius - 1, -2);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 0, 0, 0, 0);

                at.getWorld().playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
            }
            for (int i = 0; i < 20; i++) {
                final Location at = MathUtils.randomCylinder(loc, radius, -2);
                Drawings.drawLine(at, at.clone().add(0, 3, 0), Particle.SMOKE_LARGE);
            }
        });
    }
}
