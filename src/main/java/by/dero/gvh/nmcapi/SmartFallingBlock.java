package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    private Entity owner = null;
    private double hx;
    private double hy;
    private double hz;

    private EntityEvent onHitEntity = null;
    private EntityEvent onEnter = null;
    public interface EntityEvent {
        void run(Entity entity);
    }

    private Runnable onHitGround = null;

    int team = -1;
    public SmartFallingBlock(Location loc, Material material) {
        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(),
                CraftMagicNumbers.getBlock(material).fromLegacyData(0));
        noclip = true;
    }

    public SmartFallingBlock(Location loc, Material material, int team) {
        this(loc, material);
        this.team = team;
    }
    
    public void B_() {
        if (this.block.getMaterial() == net.minecraft.server.v1_12_R1.Material.AIR) {
            this.die();
        } else {
            World bukkitWorld = world.world;
            Location location = new Location(bukkitWorld, locX, locY, locZ);
            Collection<Entity> entities = bukkitWorld.getNearbyEntities(location, 0.5, 0.5, 0.5);

            for (Entity entity : entities) {
                if (entity.getUniqueId().equals(getUniqueID())) {
                    continue;
                }
                if (owner != null && owner.getUniqueId().equals(entity.getUniqueId())) {
                    continue;
                }
                if (holdEntity != null && holdEntity.getUniqueID().equals(entity.getUniqueId())) {
                    continue;
                }
                if (team != -1 && !GameUtils.isEnemy(entity, team)) {
                    continue;
                }
                onEnter.run(entity);
                break;
            }

            if (stopped) {
                return;
            }

            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;

            if (holdEntity != null) {
                this.locX += holdEntity.locX - hx;
                this.locY += holdEntity.locY - hy;
                this.locZ += holdEntity.locZ - hz;
                hx = holdEntity.locX;
                hy = holdEntity.locY;
                hz = holdEntity.locZ;
                positionChanged = true;
            } else {
                this.motY -= 0.03999999910593033D;

                super.move(EnumMoveType.SELF, motX, motY, motZ);

                for (Entity entity : entities) {
                    if (entity.getUniqueId().equals(getUniqueID())) {
                        continue;
                    }

                    if (owner != null && owner.getUniqueId().equals(entity.getUniqueId())) {
                        continue;
                    }
                    if (team != -1 && !GameUtils.isEnemy(entity, team)) {
                        continue;
                    }
                    onHitEntity.run(entity);
                    break;
                }

                if (isOnGround()) {
                    onHitGround.run();
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

    private boolean dying = false;
    public void dieLater(int delay) {
        if (dying) {
            return;
        }
        dying = true;
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
        velocityChanged = true;
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public EntityEvent getOnHitEntity() {
        return onHitEntity;
    }

    public void setOnHitEntity(EntityEvent onHitEntity) {
        this.onHitEntity = onHitEntity;
    }

    public Entity getHoldEntity() {
        return holdEntity.bukkitEntity;
    }

    public void setHoldEntity(Entity holdEntity) {
        this.holdEntity = ((CraftEntity) holdEntity).getHandle();
        hx = this.holdEntity.locX;
        hy = this.holdEntity.locY;
        hz = this.holdEntity.locZ;
    }

    public Runnable getOnHitGround() {
        return onHitGround;
    }

    public void setOnHitGround(Runnable onHitGround) {
        this.onHitGround = onHitGround;
    }

    public EntityEvent getOnEnter() {
        return onEnter;
    }

    public void setOnEnter(EntityEvent onEnter) {
        this.onEnter = onEnter;
    }
}
