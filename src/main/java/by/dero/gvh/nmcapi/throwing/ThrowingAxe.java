package by.dero.gvh.nmcapi.throwing;

import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ThrowingAxe extends ThrowingItem {
    public ThrowingAxe(Player player, Material material) {
        super(player.getLocation(), material);
        setOwner(player);
        setItemLength(0.55);
        setCenter(0.25);
        setSpinning(25);
        rightArmPose = new Vector3f(270,11,0);
        setLiveTimeAfterStop(120);
        setVelocity(player.getLocation().getDirection().add(new Vector(0,0.1,0)).normalize().multiply(1.5));
    }
}
