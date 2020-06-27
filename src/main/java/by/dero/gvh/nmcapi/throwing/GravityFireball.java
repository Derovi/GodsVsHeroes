package by.dero.gvh.nmcapi.throwing;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;

public class GravityFireball extends EntityArmorStand {
    private Runnable onHit = null;
    private final ArmorStand armorStand;
    private final SmallFireball fireball;

    public GravityFireball(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setYawPitch(loc.getYaw(), loc.getPitch());
        this.setInvisible(true);
        this.setInvulnerable(true);
        armorStand = (ArmorStand) getBukkitEntity();
        fireball = (SmallFireball) loc.getWorld().spawnEntity(loc, EntityType.SMALL_FIREBALL);
        armorStand.addPassenger(fireball);
    }

    @Override
    public void move(EnumMoveType moveType, double x, double y, double z) {
        Vector vector = new Vector(x, y, z);
        double length = vector.length();

        int stepCount = (int) (length / 0.5) + 1;

        int step;
        for (step = 0; step < stepCount; ++ step) {
            locX += x / stepCount;
            locY += y / stepCount;
            locZ += z / stepCount;
            //super.move(moveType, x, y, z);
            Location itemLocation = armorStand.getLocation().clone().add(0,3,0);
            if (itemLocation.getY() < 0 || armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType() != Material.AIR) {
                if (onHit != null) {
                    onHit.run();
                }
                fireball.remove();
                this.die();
                break;
            }
        }
    }

    public void setVelocity(Vector vector) {
        armorStand.setVelocity(vector);
    }

    public void spawn() {
        ((CraftWorld) armorStand.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public SmallFireball getFireball() {
        return fireball;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public Runnable getOnHit() {
        return onHit;
    }

    public void setOnHit(Runnable onHit) {
        this.onHit = onHit;
    }
}
