package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.EntityFireworks;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class InstantFirework extends EntityFireworks {
    public InstantFirework(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
        expectedLifespan = 2;
    }

    public void setMeta(FireworkMeta meta) {
        ((CraftFirework) getBukkitEntity()).item.setItemMeta(meta);
    }

    public FireworkMeta getMeta() {
        return (FireworkMeta) ((CraftFirework) getBukkitEntity()).item.getItemMeta();
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
