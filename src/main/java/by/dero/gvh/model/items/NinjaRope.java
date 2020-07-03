package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.NinjaRopeInfo;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class NinjaRope extends Item implements PlayerInteractInterface, ProjectileHitInterface {
    private final double forceMultiplier;

    public NinjaRope(String name, int level, Player owner) {
        super(name, level, owner);
        NinjaRopeInfo info = (NinjaRopeInfo) getInfo();
        forceMultiplier = info.getForceMultiplier();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        EntityPlayer player = ((CraftPlayer) owner).getHandle();
        EntityFishingHook fishingHook = new EntityFishingHook(player.world, player);
        Arrow arrow = (Arrow) GameUtils.spawnProjectile(owner.getEyeLocation(), 2, EntityType.ARROW, owner);
        fishingHook.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        arrow.addPassenger(fishingHook.getBukkitEntity());
        player.world.addEntity(fishingHook, CreatureSpawnEvent.SpawnReason.CUSTOM);

        summonedEntityIds.add(arrow.getUniqueId());
    }

    @Override
    public void onProjectileHit (ProjectileHitEvent event) {
        Location at = event.getEntity().getLocation();

        Vector force = at.clone().subtract(owner.getLocation()).multiply(forceMultiplier).toVector();
        force.y = Math.max(force.y, 1.5);
        owner.setVelocity(force);
    }

    @Override
    public void onProjectileHitEnemy (ProjectileHitEvent event) {

    }
}
