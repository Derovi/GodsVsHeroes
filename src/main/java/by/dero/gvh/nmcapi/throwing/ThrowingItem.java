package by.dero.gvh.nmcapi.throwing;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ThrowingItem extends EntityArmorStand {
    private final ArmorStand armorStand;
    private Entity owner = null;
    private Entity holdEntity = null;
    private boolean stopped = false;
    private boolean removed = false;
    private int liveTimeAfterStop = 0;
    private double xDelta;
    private double yDelta;
    private double zDelta;
    private double xDir;
    private double yDir;
    private double zDir;
    private double lenDir;
    private double itemLength;
    private double spinning = 0;
    private double step = 0.40;
    private Runnable onHitEntity = null;
    private Runnable onHitBlock = null;
    private Runnable onDisappear = null;
    private Runnable onOwnerPickUp = null;
    private boolean physicsSpin = false;

    public ThrowingItem(Location loc, Material material) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        armorStand = (ArmorStand) getBukkitEntity();
        xDir = loc.getDirection().getX();
        yDir = loc.getDirection().getY();
        zDir = loc.getDirection().getZ();
        lenDir = Math.sqrt(xDir * xDir + yDir * yDir + zDir * zDir);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setYawPitch(loc.getYaw(), loc.getPitch());
        this.setRightArmPose(new Vector3f(loc.getPitch(),0, 0));
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.noclip = true;
        setItem(new ItemStack(material));
    }

    public void setRotation(EulerAngle eulerAngle) {
        armorStand.setRightArmPose(eulerAngle);
    }

    public void setItem(ItemStack item) {
        armorStand.setItemInHand(item);
    }

    public void setVelocity(Vector vector) {
        armorStand.setVelocity(vector);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public Position getItemPosition() {
        if (spinning != 0 || physicsSpin) {
            double rotX = armorStand.getLocation().getPitch();
            double rotY = rightArmPose.x;
            Vector vector = new Vector();
            vector.setY(-Math.sin(Math.toRadians(rotY)));
            double xz = Math.cos(Math.toRadians(rotY));
            vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
            vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
            vector.normalize();
            return new Position(locX - zDir / 2 + vector.getX() * itemLength,
                    locY + 1 + vector.getY() * itemLength,
                    locZ + xDir / 2 + vector.getZ() * itemLength);
        }
        return new Position(locX - zDir / 2 + xDir / lenDir * itemLength,
                locY + 1 + yDir / lenDir * itemLength,
                locZ + xDir / 2 + zDir / lenDir * itemLength);
    }

    private void stop() {
        if (stopped)  {
            return;
        }
        stopped = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed) {
                    return;
                }
                remove();
                if (onDisappear != null) {
                    onDisappear.run();
                }
            }
        }.runTaskLater(Plugin.getInstance(), liveTimeAfterStop);
    }

    public void remove() {
        removed = true;
        armorStand.remove();
    }

    public boolean isRemoved() {
        return removed;
    }

    public double getItemLength() {
        return itemLength;
    }

    public void setItemLength(double itemLength) {
        this.itemLength = itemLength;
    }

    @Override
    public void move(EnumMoveType moveType, double x, double y, double z) {
        if (isStopped()) {
            if (holdEntity != null) {
                locX = holdEntity.getLocation().getX() + xDelta;
                locY = holdEntity.getLocation().getY() + yDelta;
                locZ = holdEntity.getLocation().getZ() + zDelta;
            }
            if (onOwnerPickUp != null) {
                Collection<Entity> entities = armorStand.getLocation().getWorld().getNearbyEntities(
                        armorStand.getLocation(), 1.25, 1.25, 1.25);
                for (Entity entity : entities) {
                    if (entity.getUniqueId().equals(owner.getUniqueId())) {
                        onOwnerPickUp.run();
                        break;
                    }
                }
            }
            return;
        }
        Vector vector = new Vector(x, y, z);
        double length = vector.length();

        int stepCount = (int) (length / step) + 1;
        if (!physicsSpin) {
            setRightArmPose(new Vector3f((float) (rightArmPose.x + spinning * length), (float) y, (float) z));
        } else {
            setRightArmPose(new Vector3f(360f - (float) Math.toDegrees(Math.asin(y / length)), (float) y, (float) z));
        }
        for (int step = 0; step < stepCount; ++ step) {
            locX += x / stepCount;
            locY += y / stepCount;
            locZ += z / stepCount;
            //super.move(moveType, x, y, z);
            Location itemLocation = getItemPosition().toLocation(armorStand.getWorld());
            if (itemLocation.getY() < 0) {
                stop();
                return;
            }
            if (armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType() != Material.AIR) {
                stop();
                if (onHitBlock != null) {
                    onHitBlock.run();
                }
                return;
            }
            Collection<Entity> entities = armorStand.getLocation().getWorld().getNearbyEntities(itemLocation, 0.05, 0.25, 0.05);
            for (Entity entity : entities) {
                if (entity.getUniqueId().equals(getUniqueID())) {
                    continue;
                }
                if (owner != null && entity.getUniqueId().equals(owner.getUniqueId())) {
                    continue;
                }
                holdEntity = entity;
                xDelta = locX - entity.getLocation().getX();
                yDelta = locY - entity.getLocation().getY();
                zDelta = locZ - entity.getLocation().getZ();
                stop();
                if (onHitEntity != null) {
                    onHitEntity.run();
                }
                return;
            }
        }
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public void spawn() {
        ((CraftWorld) armorStand.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public boolean isPhysicsSpin() {
        return physicsSpin;
    }

    public void setPhysicsSpin(boolean physicsSpin) {
        this.physicsSpin = physicsSpin;
    }

    public Entity getHoldEntity() {
        return holdEntity;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public int getLiveTimeAfterStop() {
        return liveTimeAfterStop;
    }

    public void setLiveTimeAfterStop(int liveTimeAfterStop) {
        this.liveTimeAfterStop = liveTimeAfterStop;
    }

    public boolean isStopped() {
        return stopped;
    }

    public Runnable getOnHitEntity() {
        return onHitEntity;
    }

    public void setOnHitEntity(Runnable onHitEntity) {
        this.onHitEntity = onHitEntity;
    }

    public Runnable getOnHitBlock() {
        return onHitBlock;
    }

    public void setOnHitBlock(Runnable onHitBlock) {
        this.onHitBlock = onHitBlock;
    }

    public double getSpinning() {
        return spinning;
    }

    public void setSpinning(double spinning) {
        this.spinning = spinning;
    }

    public Runnable getOnDisappear() {
        return onDisappear;
    }

    public void setOnDisappear(Runnable onDisappear) {
        this.onDisappear = onDisappear;
    }

    public Runnable getOnOwnerPickUp() {
        return onOwnerPickUp;
    }

    public void setOnOwnerPickUp(Runnable onOwnerPickUp) {
        this.onOwnerPickUp = onOwnerPickUp;
    }
}