package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.GrenadeInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Grenade extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, PlayerInteractInterface {
    private final double radius;
    private final double damage;
    private final Material material;

    public Grenade(final String name, final int level, final Player owner) {
        super(name, level, owner);
        GrenadeInfo info = (GrenadeInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
        material = info.getMaterial();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
        for (final LivingEntity ent : GameUtils.getNearby(loc, radius)) {
            if (GameUtils.isEnemy(ent, getTeam())) {
                GameUtils.damage(damage, ent, owner);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (ownerGP.isCharged(getName())) {
            owner.setCooldown(material, (int) cooldown.getDuration());
        }
        final Projectile proj = SpawnUtils.spawnProjectile(owner.getEyeLocation(),
                1.2, EntityType.SNOWBALL, owner);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
        summonedEntityIds.add(proj.getUniqueId());
    }
}
