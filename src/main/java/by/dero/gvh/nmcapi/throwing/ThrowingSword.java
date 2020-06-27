package by.dero.gvh.nmcapi.throwing;

import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ThrowingSword extends ThrowingItem {
    public ThrowingSword(Player player, Material material) {
        super(player.getLocation(), material);
        setOwner(player);
        setItemLength(0.7);
        setPhysicsSpin(true);
        setRightArmPose(new Vector3f(player.getLocation().getPitch(),11, 0));
        setLiveTimeAfterStop(120);
        setVelocity(player.getLocation().getDirection().add(new Vector(0,0.1,0)).normalize().multiply(1.5));
    }
}
