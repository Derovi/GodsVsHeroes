package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChaseEnemyInfo;
import by.dero.gvh.utils.DataUtils;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
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
        Location loc = owner.getLocation();

        EntityZombie zombie = new EntityZombie(((CraftWorld) owner.getWorld()).world);
        zombie.setPosition(loc.x, loc.y, loc.z);

        zombie.targetSelector.b.clear();
        Predicate<EntityPlayer> pred = (pl) -> DataUtils.isEnemy(pl.getBukkitEntity(), getTeam());
        zombie.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(
                zombie, EntityPlayer.class, 1, true, false, pred));
        setAttributes(zombie);
        cooldown.reload();
        zombie.getBukkitEntity().addPassenger(owner);
        zombie.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        zombie.world.addEntity(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Bukkit.getServer().broadcastMessage("1");
        BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = duration;
            @Override
            public void run() {
                ticks -= 5;
                if (!owner.isOnline() || !owner.getGameMode().equals(GameMode.SURVIVAL) ||
                        ticks < 0 || zombie.passengers.isEmpty() ||
                        DataUtils.getNearestEnemyPlayer(DataUtils.getPlayer(owner.getName())).
                                getPlayer().getLocation().distance(owner.getLocation()) < 2) {
                    zombie.die();
                    this.cancel();
                    Bukkit.getServer().broadcastMessage("4");
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 5);
        Game.getInstance().getRunnables().add(runnable);
    }
}
