package by.dero.gvh.nmcapi.dragon;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class RotatingDragon extends EntityEnderDragon {
    public RotatingDragon(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public void r() { }

    @Override
    public void B_() {
        /*float pitch = player.getLocation().getPitch();
        if (player.getLocation().getPitch() < 0.0f) {
            pitch = 0.0f;
        }
        else if (player.getLocation().getPitch() > 60.0f) {
            pitch = 60.0f;
        }
        final Vector vec = player.getLocation().getDirection().multiply(1).setY(pitch / 40.0f * -1.0f + 0.75);

        yaw = player.getLocation().getYaw() - 180.0f;*/
    }

    public void setRotation(float yaw) {
        this.yaw = yaw;
    }

    public void spawn() {
        getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
