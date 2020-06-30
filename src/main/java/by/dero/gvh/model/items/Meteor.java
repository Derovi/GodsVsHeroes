package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.MeteorInfo;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityLargeFireball;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;

public class Meteor extends Item implements PlayerInteractInterface, ProjectileHitInterface {
    private final double damage;
    private final double radius;
    private final int range;

    public Meteor(String name, int level, Player owner) {
        super(name, level, owner);
        MeteorInfo info = (MeteorInfo) getInfo();
        damage = info.getDamage();
        radius = info.getRadius();
        range = info.getRange();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        BlockIterator it = new BlockIterator(owner.getEyeLocation(), 0, range);
        Location at = null;
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType().equals(Material.AIR)) {
                continue;
            }
            at = block.getLocation().clone();
            break;
        }
        if (at == null) {
            return;
        }
        cooldown.reload();
        at.add(0, 10, 0);
        Drawings.drawLine(at.clone().add(radius, 0, 0), at.clone().add(-radius, 0, 0), Particle.SMOKE_LARGE);
        Drawings.drawLine(at.clone().add(0, 0, radius), at.clone().add(0, 0, -radius), Particle.SMOKE_LARGE);
        Drawings.drawCircle(at, radius-1, Particle.FLAME);

        EntityLargeFireball fireball = new EntityLargeFireball(((CraftWorld) owner.getWorld()).world);

        fireball.isIncendiary = false;
        fireball.projectileSource = owner;
        fireball.setPosition(at.x, at.y, at.z);
        fireball.dirX = 0;
        fireball.dirY = -0.2;
        fireball.dirZ = 0;
        fireball.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        fireball.world.addEntity(fireball, CreatureSpawnEvent.SpawnReason.CUSTOM);
        summonedEntityIds.add(fireball.uniqueID);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Location loc = event.getEntity().getLocation();
        owner.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 0, 0, 0, 0);
        for (LivingEntity entity : GameUtils.getNearby(loc, radius)) {
            if (GameUtils.isEnemy(entity, getTeam())) {
                GameUtils.damage(damage, entity, owner);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}
