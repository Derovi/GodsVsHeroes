package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.NinjaRopeInfo;
import by.dero.gvh.nmcapi.InfiniteFishHook;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NinjaRope extends Item implements PlayerInteractInterface, ProjectileHitInterface {
    private final double forceMultiplier;
    private final double distance;

    public NinjaRope(String name, int level, Player owner) {
        super(name, level, owner);
        NinjaRopeInfo info = (NinjaRopeInfo) getInfo();
        forceMultiplier = info.getForceMultiplier();
        distance = info.getDistance();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        EntityPlayer player = ((CraftPlayer) owner).getHandle();
        InfiniteFishHook fishingHook = new InfiniteFishHook(player.world, player);
        Arrow arrow = (Arrow) GameUtils.spawnProjectile(owner.getEyeLocation(), 2, EntityType.ARROW, owner);
        fishingHook.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        arrow.addPassenger(fishingHook.getBukkitEntity());
        player.world.addEntity(fishingHook, CreatureSpawnEvent.SpawnReason.CUSTOM);

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
                    owner.getWorld().playSound(arrow.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.7f, 1);
                    fishingHook.die();
                    arrow.remove();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
        Game.getInstance().getRunnables().add(runnable);
    }

    @Override
    public void onProjectileHit (ProjectileHitEvent event) {
        event.getEntity().remove();
        Location at = event.getEntity().getLocation();

        Vector force = at.clone().subtract(owner.getLocation()).multiply(forceMultiplier).toVector();

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.7f, 1);
        force.y = Math.max(force.y, 0.7);
        force.y = Math.min(force.y, 1.8);
        owner.setVelocity(force);
    }

    @Override
    public void onProjectileHitEnemy (ProjectileHitEvent event) {

    }
}
