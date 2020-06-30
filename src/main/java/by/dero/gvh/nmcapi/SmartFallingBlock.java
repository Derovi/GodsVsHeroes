package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SmartFallingBlock extends EntityFallingBlock {
    private boolean stopped = false;
    private net.minecraft.server.v1_12_R1.Entity holdEntity = null;

    private EntityHitEvent onHitEntity = null;
    public interface EntityHitEvent {
        void run(Entity entity);
    }

    private BlockHitEvent onHitBlock = null;
    public interface BlockHitEvent {
        void run(Block block);
    }

    public SmartFallingBlock(Location loc, Material material) {
        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(),
                CraftMagicNumbers.getBlock(material).fromLegacyData(0));
        noclip = true;
    }

    public boolean isNotVoid(Material material) {
        switch (material) {
            case AIR:
            case SAPLING:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case LONG_GRASS:
            case DEAD_BUSH:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TORCH:
            case FIRE:
            case REDSTONE_WIRE:
            case CROPS:
            case LADDER:
            case RAILS:
            case LEVER:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case SNOW:
            case SUGAR_CANE_BLOCK:
            case WATER_LILY:
            case TRIPWIRE:
            case FLOWER_POT:
            case CARROT:
            case POTATO:
            case WOOD_BUTTON:
            case ACTIVATOR_RAIL:
            case CARPET:
            case DOUBLE_PLANT:
            case END_ROD:
            case CHORUS_PLANT:
            case CHORUS_FLOWER:
            case BEETROOT_BLOCK:
                return false;
            default:
                return true;
        }
    }

    public void B_() {
        if (this.block.getMaterial() == net.minecraft.server.v1_12_R1.Material.AIR) {
            this.die();
        } else {
            if (stopped) {
                return;
            }

            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.motY -= 0.03999999910593033D;
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);

            this.motX *= 0.9800000190734863D;
            this.motY *= 0.9800000190734863D;
            this.motZ *= 0.9800000190734863D;

            if (holdEntity != null) {
                this.locX += holdEntity.lastX - holdEntity.locX;
                this.locY += holdEntity.lastY - holdEntity.locY;
                this.locZ += holdEntity.lastZ - holdEntity.locZ;
            } else {
                this.motY -= 0.03999999910593033D;

                locX += motX;
                locY += motY;
                locZ += motZ;

                World bukkitWorld = world.world;
                Location location = new Location(bukkitWorld, locX, locY, locZ);

                Collection<Entity> entities = bukkitWorld.getNearbyEntities(location, 0.5, 0.5, 0.5);
                for (Entity entity : entities) {
                    if (entity.getUniqueId().equals(getUniqueID())) {
                        continue;
                    }
                    onHitEntity.run(entity);
                    break;
                }

                Block block = new Location(bukkitWorld, locX - 0.5, locY - 0.5, locZ).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX + 0.5, locY - 0.5, locZ).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX - 0.5, locY + 0.5, locZ).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX + 0.5, locY + 0.5, locZ).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX, locY - 0.5, locZ - 0.5).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX, locY - 0.5, locZ + 0.5).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX, locY + 0.5, locZ - 0.5).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }
                block = new Location(bukkitWorld, locX, locY + 0.5, locZ + 0.5).getBlock();
                if (isNotVoid(block.getType())) {
                    onHitBlock.run(block);
                }

                this.motX *= 0.9800000190734863D;
                this.motY *= 0.9800000190734863D;
                this.motZ *= 0.9800000190734863D;
            }

            if (locY < 0) {
                die();
            }
        }
    }

    public void dieLater(int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                die();
            }
        }.runTaskLater(Plugin.getInstance(), delay);
    }

    public void setVelocity(Vector velocity) {
        motX = velocity.getX();
        motY = velocity.getY();
        motZ = velocity.getZ();
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public EntityHitEvent getOnHitEntity() {
        return onHitEntity;
    }

    public void setOnHitEntity(EntityHitEvent onHitEntity) {
        this.onHitEntity = onHitEntity;
    }

    public BlockHitEvent getOnHitBlock() {
        return onHitBlock;
    }

    public void setOnHitBlock(BlockHitEvent onHitBlock) {
        this.onHitBlock = onHitBlock;
    }

    public Entity getHoldEntity() {
        return holdEntity.bukkitEntity;
    }

    public void setHoldEntity(Entity holdEntity) {
        this.holdEntity = ((CraftEntity) holdEntity).getHandle();
    }
}
