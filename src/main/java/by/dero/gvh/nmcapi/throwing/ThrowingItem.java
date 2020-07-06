package by.dero.gvh.nmcapi.throwing;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
    private boolean physicsSpin = false;
    private double expX;
    private double expY;
    private double expZ;
    private double sizeMultiplier = 1.0;

    public ThrowingItem(Location loc, Material material) {
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
        setItem(new ItemStack(material));
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

    public boolean isVoid(Material material) {
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
                return true;
            default:
                return false;
        }
    }

    private boolean isDeadPlayer(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return player.getGameMode().equals(GameMode.SPECTATOR);
        }
        return false;
    }

    @Override
    public void move(EnumMoveType moveType, double x, double y, double z) {
        if (isStopped()) {
            if (holdEntity != null) {
                if (holdEntity.isDead() || isDeadPlayer(holdEntity)) {
                    holdEntity = null;
                } else {
                    locX = holdEntity.getLocation().getX() + xDelta;
                    locY = holdEntity.getLocation().getY() + yDelta;
                    locZ = holdEntity.getLocation().getZ() + zDelta;
                }
            }
            if (holdEntity == null) {
                Location itemLocation = getItemPosition().toLocation(armorStand.getWorld());
                if (isVoid(armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType())) {
                    locY -= 0.1;
                }
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
            if (!isVoid(armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType())) {
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
}