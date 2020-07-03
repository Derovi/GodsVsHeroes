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
    public void B_() { }

    public void setRotation(float yaw) {
        this.yaw = yaw;
    }

    public void spawn() {
        getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
