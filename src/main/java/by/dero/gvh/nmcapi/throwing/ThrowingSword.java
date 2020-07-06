package by.dero.gvh.nmcapi.throwing;

import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ThrowingSword extends ThrowingItem {
    public ThrowingSword(Player player, Material material) {
        super(player.getLocation(), material);
        setOwner(player);
        setItemLength(0.8);
        setPhysicsSpin(true);
        setRightArmPose(new Vector3f(player.getLocation().getPitch(),11, 0));
        setLiveTimeAfterStop(120);
        setVelocity(player.getLocation().getDirection().add(new Vector(0,0.2,0)).normalize().multiply(2));
    }
}
