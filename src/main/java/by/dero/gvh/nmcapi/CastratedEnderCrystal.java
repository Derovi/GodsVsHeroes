package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.GameProfileSerializer;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CastratedEnderCrystal extends EntityEnderCrystal {
    public CastratedEnderCrystal(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        setShowingBottom(false);
    }

    @Override
    public void B_() {}

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
