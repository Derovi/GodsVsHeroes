package by.dero.gvh.nmcapi.throwing;

import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ThrowingKnife extends ThrowingItem {
    public ThrowingKnife(Player player, Material material) {
        super(player.getLocation(), material);
        setOwner(player);
        setSmall(true);
        setItemLength(0.4);
        setPhysicsSpin(true);
        setRightArmPose(new Vector3f(player.getLocation().getPitch(),11, 0));
        setLiveTimeAfterStop(80);
        setVelocity(player.getLocation().getDirection().add(new Vector(0,0.2,0)).normalize().multiply(2));
    }
}
