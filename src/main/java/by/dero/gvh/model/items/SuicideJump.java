package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.SuicideJumpInfo;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftProjectile;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.*;

public class SuicideJump extends Item implements PlayerInteractInterface, ProjectileHitInterface {
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
        final Projectile proj = spawnProjectile(player.getEyeLocation(), 0.8, EntityType.SNOWBALL, player);
        ((CraftProjectile) proj).getHandle().noclip = true;
        summonedEntityIds.add(proj.getUniqueId());
        proj.addPassenger(player);
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Projectile proj = event.getEntity();
        final Player player = (Player) proj.getShooter();
        proj.removePassenger(player);
        damage(selfDamage, player, player);
        for (final LivingEntity entity : getNearby(proj.getLocation(), radius)) {
            if (isEnemy(entity, team)) {
                damage(damage, entity, player);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}
