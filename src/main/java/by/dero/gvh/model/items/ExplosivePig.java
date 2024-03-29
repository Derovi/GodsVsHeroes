package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExplosivePigInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.PathfinderFollow;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPig;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
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
    private final Material material;

    public ExplosivePig(String name, int level, Player owner) {
        super(name, level, owner);
        ExplosivePigInfo info = (ExplosivePigInfo) getInfo();
        duration = info.getDuration();
        radius = info.getRadius();
        damage = info.getDamage();
        material = info.getMaterial();
    }

    private void setAttributes(EntityPig pig) {
        pig.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(1000);
        pig.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
        pig.getAttributeInstance(GenericAttributes.maxHealth).setValue(20);
        pig.setHealth(20);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }

        GamePlayer targetGP = GameUtils.getNearestEnemyPlayer(ownerGP);
        if (targetGP == null) {
            owner.sendMessage(Lang.get("game.noEnemyTarget"));
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());

        EntityPig pig = new EntityPig(((CraftWorld) owner.getWorld()).world);
        Location pLoc = owner.getLocation();
        pig.setPosition(pLoc.getX(), pLoc.getY(), pLoc.getZ());
        setAttributes(pig);

        pig.goalSelector = new PathfinderGoalSelector(pig.world.methodProfiler);
        pig.targetSelector = new PathfinderGoalSelector(pig.world.methodProfiler);

        CraftPlayer target = (CraftPlayer) targetGP.getPlayer();
        pig.setGoalTarget(target.getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        pig.goalSelector.a(0, new PathfinderFollow(pig, 1, 200));

        pig.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        pig.world.addEntity(pig, CreatureSpawnEvent.SpawnReason.CUSTOM);
        GameMob gm = new GameMob((LivingEntity) pig.getBukkitEntity(), getTeam(), owner);
        gm.updateName();
        BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = duration;
            GamePlayer cur = targetGP;
            @Override
            public void run() {
                ticks -= 3;
                Location loc = pig.getBukkitEntity().getLocation();

                GamePlayer nxt = GameUtils.getNearestEnemyPlayer(GameUtils.getMob(pig.uniqueID));
                if (cur != nxt) {
                    cur = nxt;
                    pig.setGoalTarget(((CraftPlayer) cur.getPlayer()).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
                }
                if (cur == null) {
                    explode(pig);
                    this.cancel();
                    return;
                }

                EntityLiving tar = pig.getGoalTarget();

                if (pig.dead || ticks < 0 || (tar != null && tar.getBukkitEntity().getLocation().distance(loc) < radius)) {
                    explode(pig);
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 3);
        Game.getInstance().getRunnables().add(runnable);
    }

    private void explode(EntityPig pig) {
        Location loc = pig.getBukkitEntity().getLocation();
        pig.die();
        pig.world.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
        owner.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.07f, 1);
        for (LivingEntity entity : GameUtils.getNearby(loc, radius)) {
            if (GameUtils.isEnemy(entity, getTeam())) {
                GameUtils.damage(damage, entity, owner);
            }
        }
    }

}
