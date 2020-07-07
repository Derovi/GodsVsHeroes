package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DFireball extends EntityDragonFireball {
    private int explodeDamage;

    public DFireball(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    public void a(MovingObjectPosition var1) {
        System.out.println("EXPLODE");
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public int getExplodeDamage() {
        return explodeDamage;
    }

    public void setExplodeDamage(int explodeDamage) {
        this.explodeDamage = explodeDamage;
    }
}
