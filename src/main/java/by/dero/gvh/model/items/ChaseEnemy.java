package by.dero.gvh.model.items;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChaseEnemyInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.PathfinderFollow;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
        GamePlayer gp = GameUtils.getNearestEnemyPlayer(ownerGP);
        if (gp == null) {
            owner.sendMessage(Lang.get("game.noEnemyTarget"));
            return;
        }
        CraftPlayer target = (CraftPlayer) gp.getPlayer();

        cooldown.reload();
        Location loc = owner.getLocation();

        EntityZombie zombie = new EntityZombie(((CraftWorld) owner.getWorld()).world);
        zombie.setPosition(loc.x, loc.y, loc.z);

        setAttributes(zombie);

        zombie.goalSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
        zombie.targetSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
        zombie.setGoalTarget(target.getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        zombie.goalSelector.a(0, new PathfinderFollow(zombie, 1, 200));
        zombie.getBukkitEntity().addPassenger(owner);
        zombie.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        zombie.world.addEntity(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setEquipment(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_CHESTPLATE)));
        BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = duration;
            @Override
            public void run() {
                ticks -= 2;
                if (!GameUtils.isInGame(owner) || ticks < 0 || zombie.passengers.isEmpty() ||
                        GameUtils.getNearestEnemyPlayer(GameUtils.getPlayer(owner.getName())).
                                getPlayer().getLocation().distance(owner.getLocation()) < 2) {
                    owner.getWorld().playSound(zombie.getBukkitEntity().getLocation(), Sound.ENTITY_HUSK_DEATH, 1.07f, 1);
                    zombie.die();
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);
        Game.getInstance().getRunnables().add(runnable);
    }
}
