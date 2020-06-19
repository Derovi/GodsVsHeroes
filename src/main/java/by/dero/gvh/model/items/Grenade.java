package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.GrenadeInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class Grenade extends Item implements InfiniteReplenishInterface,
        PlayerInteractInterface, ProjectileHitInterface, ProjectileLaunchInterface {
    private final double radius;
    private final double damage;

    public Grenade(final String name, final int level, final Player owner) {
        super(name, level, owner);
        GrenadeInfo info = (GrenadeInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
        for (LivingEntity ent : getNearby(loc, radius)) {
            if (isEnemy(ent, getTeam())) {
                ent.damage(damage, getOwner());
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!cooldown.isReady()) {
            if (System.currentTimeMillis() - cooldown.getStartTime() > 100) {
                sendCooldownMessage(getOwner(), getInfo().getDisplayName(), cooldown.getSecondsRemaining());
            }
            return;
        }
        cooldown.reload();
    }
}
