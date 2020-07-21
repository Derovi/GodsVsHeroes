package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.NinjaRopeInfo;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NinjaRope extends Item implements ProjectileHitInterface, ProjectileLaunchInterface {
    private final double forceMultiplier;
    private final double distance;
    private final Material material;

    public NinjaRope(String name, int level, Player owner) {
        super(name, level, owner);
        NinjaRopeInfo info = (NinjaRopeInfo) getInfo();
        forceMultiplier = info.getForceMultiplier();
        distance = info.getDistance();
        material = info.getMaterial();
    }

    @Override
    public void onProjectileHit (ProjectileHitEvent event) {
        for (Entity ent : event.getEntity().getPassengers()) {
            ent.remove();
        }
        event.getEntity().remove();
        Location at = event.getEntity().getLocation();

        Vector force = at.clone().subtract(owner.getLocation()).multiply(forceMultiplier).toVector();

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.07f, 1);
        force.y = Math.max(force.y, 0.7);
        force.y = Math.min(force.y, 1.8);
        owner.setVelocity(force);
    }

    @Override
    public void onProjectileHitEnemy (ProjectileHitEvent event) {

    }
    
    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!cooldown.isReady()) {
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
        Arrow arrow = (Arrow) SpawnUtils.spawnProjectile(owner.getEyeLocation(), 2, EntityType.ARROW, owner);
        arrow.addPassenger(event.getEntity());
        summonedEntityIds.add(event.getEntity().getUniqueId());
        summonedEntityIds.add(arrow.getUniqueId());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run () {
                if (arrow.isDead()) {
                    this.cancel();
                    return;
                }
                if (arrow.getLocation().distance(owner.getLocation()) > distance) {
                    owner.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, arrow.getLocation(), 0, 0, 0, 0);
                    owner.getWorld().playSound(arrow.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.07f, 1);
                    event.getEntity().remove();
                    arrow.remove();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
        Game.getInstance().getRunnables().add(runnable);
    }
}
