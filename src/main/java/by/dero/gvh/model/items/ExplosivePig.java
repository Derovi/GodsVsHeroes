package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExplosiveBowInfo;
import by.dero.gvh.model.itemsinfo.ExplosivePigInfo;
import by.dero.gvh.utils.GameUtils;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPig;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ExplosivePig extends Item implements PlayerInteractInterface {
    private final int duration;
    private final double radius;
    private final double damage;

    public ExplosivePig(String name, int level, Player owner) {
        super(name, level, owner);
        ExplosivePigInfo info = (ExplosivePigInfo) getInfo();
        duration = info.getDuration();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    private void setAttributes(EntityPig pig) {
        pig.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(1000);
        pig.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
        pig.setInvulnerable(true);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();

        EntityPig pig = new EntityPig(((CraftWorld) owner.getWorld()).world);
        Location pLoc = owner.getLocation();
        pig.setPosition(pLoc.getX(), pLoc.getY(), pLoc.getZ());
        setAttributes(pig);

        pig.targetSelector.b.clear();
        pig.goalSelector.b.clear();
        CraftPlayer target = (CraftPlayer) GameUtils.getNearestEnemyPlayer(GameUtils.getPlayer(owner.getName())).getPlayer();
        Predicate<EntityPlayer> pred = (pl) -> GameUtils.isEnemy(pl.getBukkitEntity(), getTeam());
        pig.targetSelector.a(0, new PathfinderGoalMoveTowardsTarget(pig, 1, 100));
        pig.setGoalTarget(target.getHandle(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);

        pig.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        pig.world.addEntity(pig, CreatureSpawnEvent.SpawnReason.CUSTOM);
        BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = duration;
            @Override
            public void run() {
                ticks -= 5;
                Location loc = pig.getBukkitEntity().getLocation();
                if (ticks < 0 || GameUtils.getNearestEnemyPlayer(GameUtils.getPlayer(owner.getName())).getPlayer().
                        getLocation().distance(loc) < radius) {
                    pig.die();
                    Bukkit.getServer().broadcastMessage(pig.getGoalTarget().getName());
                    pig.world.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                    for (LivingEntity entity : GameUtils.getNearby(loc, radius)) {
                        if (GameUtils.isEnemy(entity, getTeam())) {
                            GameUtils.damage(damage, entity, owner);
                        }
                    }
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 3);
        Game.getInstance().getRunnables().add(runnable);
    }
}
