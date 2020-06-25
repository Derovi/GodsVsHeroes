package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class SmartArmorStand extends EntityArmorStand {
    public SmartArmorStand(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
    }
}
