package by.dero.gvh.nmcapi.throwing;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
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
    private double zDir;
    private double itemLength;
    private double spinning = 0;
    private double step = 0.40;
    private double center = 0;
    private Runnable onHitEntity = null;
    private Runnable onHitBlock = null;
    private Runnable onDisappear = null;
    private Runnable onOwnerPickUp = null;
    private Runnable onReturned = null;
    private boolean physicsSpin = false;
    private double expX;
    private double expY;
    private double expZ;
    private double sizeMultiplier = 1.0;
    private boolean returning = false;

    public ThrowingItem(Location loc, ItemStack itemStack) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        armorStand = (ArmorStand) getBukkitEntity();
        xDir = loc.getDirection().getX();
        zDir = loc.getDirection().getZ();
        double lenDir = Math.sqrt(xDir * xDir + zDir * zDir);
        xDir /= lenDir;
        zDir /= lenDir;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setYawPitch(loc.getYaw(), loc.getPitch());
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.noclip = true;
        setItem(itemStack);
        expX = locX;
        expY = locY;
        expZ = locZ;
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
            double rotX = armorStand.getLocation().getYaw();
            double rotY = rightArmPose.x;
            Vector vector = new Vector();
            vector.setY(-Math.sin(Math.toRadians(rotY)));
            double xz = Math.cos(Math.toRadians(rotY));
            vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
            vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
            vector.normalize();

            Vector vector2 = new Vector();
            vector2.setY(-Math.sin(Math.toRadians(rotY + 90)));
            double xz2 = Math.cos(Math.toRadians(rotY + 90));
            vector2.setX(-xz2 * Math.sin(Math.toRadians(rotX)));
            vector2.setZ(xz2 * Math.cos(Math.toRadians(rotX)));
            vector2.normalize();

            double vx = vector.getX() * itemLength + vector2.getX() * 0.7 * sizeMultiplier;
            double vy = vector.getY() * itemLength + vector2.getY() * 0.7 * sizeMultiplier;
            double vz = vector.getZ() * itemLength + vector2.getZ() * 0.7 * sizeMultiplier;

            return new Position(locX - zDir * 0.45 * sizeMultiplier + vx,
                    locY + 1.5 * sizeMultiplier + vy,
                    locZ + xDir * 0.45 * sizeMultiplier + vz);
        }
        return null;
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
        if (!armorStand.isDead()) {
            armorStand.remove();
        }
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
    public void B_() {
        if (GameUtils.isDeadPlayer(owner)) {
            remove();
            return;
        }
        if (returning) {
            Vector dist = new Vector(owner.getLocation().getX() - locX,
                    owner.getLocation().getY() - locY,
                    owner.getLocation().getZ() - locZ);
            float curRot = rightArmPose.x;
            while (curRot < 270) {
                curRot += 360;
            }
            double length = dist.length();
            float speed = 1.5f;
            if (length < 8) {
                speed = 0.12f + 1.38f * (float) (Math.sqrt(Math.max(0, (float) length - 0.3f)) / Math.sqrt(2.775));
            }
            curRot -= (curRot - 270) / (length / speed);
            setRightArmPose(new Vector3f(curRot, 0, 0));
            if (length < 0.1) {
                if (onReturned != null) {
                    onReturned.run();
                }
                die();
                return;
            }
            dist.normalize().multiply(speed);
            motX = (float) dist.x;
            motY = (float) dist.y;
            motZ = (float) dist.z;
            velocityChanged = true;
            locX += dist.x;
            locY += dist.y;
            locZ += dist.z;
        } else {
            super.B_();
        }
    }

    @Override
    public void move(EnumMoveType moveType, double x, double y, double z) {
        if (isStopped()) {
            if (holdEntity != null) {
                if (holdEntity.isDead() || GameUtils.isDeadPlayer(holdEntity)) {
                    holdEntity = null;
                } else {
                    locX = holdEntity.getLocation().getX() + xDelta;
                    locY = holdEntity.getLocation().getY() + yDelta;
                    locZ = holdEntity.getLocation().getZ() + zDelta;
                }
            }
            if (holdEntity == null) {
                Location itemLocation = getItemPosition().toLocation(armorStand.getWorld());
                if (GameUtils.isVoid(armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType())) {
                    locY -= 0.1;
                }
            }
            if (onOwnerPickUp != null) {
                Collection<Entity> entities = armorStand.getLocation().getWorld().getNearbyEntities(
                        armorStand.getLocation(), 1.25, 1.25, 1.25);
                for (Entity entity : entities) {
                    if (entity.getUniqueId().equals(owner.getUniqueId()) || entity.getPassengers().contains(owner)) {
                        onOwnerPickUp.run();
                        break;
                    }
                }
            }
            return;
        }
        if (center != 0) {
            expX += x;
            expY += y;
            expZ += z;

            double rotX = armorStand.getLocation().getPitch();
            double rotY = rightArmPose.x;
            Vector vector = new Vector();
            vector.setY(-Math.sin(Math.toRadians(rotY)));
            double xz = Math.cos(Math.toRadians(rotY));
            vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
            vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
            vector.normalize();
            vector.multiply(itemLength * center);

            x = expX + vector.getX() - locX;
            y = expY + vector.getY() - locY;
            z = expZ + vector.getZ() - locZ;
        }

        Vector vector = new Vector(x, y, z);
        double length = vector.length();

        int stepCount = (int) (length / step) + 1;

        int step;
        boolean stopSteps = false;
        for (step = 0; step < stepCount; ++ step) {
            locX += x / stepCount;
            locY += y / stepCount;
            locZ += z / stepCount;
            //super.move(moveType, x, y, z);
            Location itemLocation = getItemPosition().toLocation(armorStand.getWorld());
            if (itemLocation.getY() < 0) {
                stop();
                break;
            }
            if (!GameUtils.isVoid(armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType())) {
                stop();
                if (onHitBlock != null) {
                    onHitBlock.run();
                }
                break;
            }
            Collection<Entity> entities = itemLocation.getWorld().getNearbyEntities(itemLocation, 0.15, 0.15, 0.15);
            for (Entity entity : entities) {
                if (entity.getUniqueId().equals(getUniqueID())) {
                    continue;
                }
                if (owner != null && entity.getUniqueId().equals(owner.getUniqueId())) {
                    continue;
                }
                if (!GameUtils.isGameEntity(entity)) {
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
                stopSteps = true;
                break;
            }
            if (stopSteps) {
                break;
            }
        }

        if (!physicsSpin) {
            setRightArmPose(new Vector3f((float) (rightArmPose.x + spinning * length / stepCount * step), rightArmPose.y, rightArmPose.z));
        } else {
            setRightArmPose(new Vector3f(360f - (float) Math.toDegrees(Math.asin(y / length)),rightArmPose.y,rightArmPose.z));
        }
    }

    @Override
    public void setSmall(boolean flag) {
        sizeMultiplier = flag ? 0.5 : 1;
        super.setSmall(flag);
    }

    public void backToOwner() {
        returning = true;
    }

    public void setHoldEntity(Entity holdEntity) {
        this.holdEntity = holdEntity;
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

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public Runnable getOnReturned() {
        return onReturned;
    }

    public void setOnReturned(Runnable onReturned) {
        this.onReturned = onReturned;
    }
}