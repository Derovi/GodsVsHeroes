package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChaseEnemyInfo;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.PathfinderFollow;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ChaseEnemy extends Item implements PlayerInteractInterface {
    private final int duration;

    public ChaseEnemy(String name, int level, Player owner) {
        super(name, level, owner);
        ChaseEnemyInfo info = (ChaseEnemyInfo) getInfo();
        duration = info.getDuration();
    }

    private void setAttributes(EntityZombie zombie) {
        zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0);
        zombie.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(1000);
        zombie.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
        zombie.setInvulnerable(true);
        zombie.setBaby(true);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        Location loc = owner.getLocation();

        EntityZombie zombie = new EntityZombie(((CraftWorld) owner.getWorld()).world);
        zombie.setPosition(loc.x, loc.y, loc.z);

        setAttributes(zombie);
        CraftPlayer target = (CraftPlayer) GameUtils.getNearestEnemyPlayer(GameUtils.getPlayer(owner.getName())).getPlayer();
//        Predicate<EntityPlayer> pred = (pl) -> GameUtils.isEnemy(pl.getBukkitEntity(), getTeam());
        zombie.goalSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
        zombie.targetSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
        zombie.setGoalTarget(target.getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        zombie.goalSelector.a(0, new PathfinderFollow(zombie, 1, 50));
        zombie.getBukkitEntity().addPassenger(owner);
        zombie.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        zombie.world.addEntity(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = duration;
            @Override
            public void run() {
                ticks -= 5;
                if (!owner.isOnline() || !owner.getGameMode().equals(GameMode.SURVIVAL) ||
                        ticks < 0 || zombie.passengers.isEmpty() ||
                        GameUtils.getNearestEnemyPlayer(GameUtils.getPlayer(owner.getName())).
                                getPlayer().getLocation().distance(owner.getLocation()) < 2) {
                    zombie.die();
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 5);
        Game.getInstance().getRunnables().add(runnable);
    }
}