package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.EntityChicken;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PassiveChicken extends EntityChicken {

    public PassiveChicken(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
        yaw = loc.getYaw();
        pitch = 90;
    }

    @Override
    public void r() { }

    @Override
    public void B_() {
        locY += 0.3;
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
