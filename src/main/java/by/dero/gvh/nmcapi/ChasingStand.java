package by.dero.gvh.nmcapi;

import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SafeRunnable;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ChasingStand extends EntityArmorStand {
	public SafeRunnable onReach = null;
	public final double speed;
	public final Player owner;
	public final double dst;
	
	public ChasingStand(World world, double d0, double d1, double d2, double speed, double dst, Player owner) {
		super(world, d0, d1, d2);
		this.owner = owner;
		this.dst = dst;
		this.speed = speed;
	}
	
	@Override
	public void move(EnumMoveType moveType, double x, double y, double z) {
		if (GameUtils.isDeadPlayer(owner)) {
			this.die();
			return;
		}
		locX += x;
		locY += y;
		locZ += z;
		Vector zxc = owner.getLocation().toVector().subtract(new Vector(locX, locY, locZ));
		if (zxc.length() < dst) {
			if (onReach != null) {
				onReach.run();
			}
			this.die();
		} else {
			yaw = owner.getLocation().yaw;
			zxc.normalize().multiply(this.speed);
			motX = zxc.x;
			motY = zxc.y;
			motZ = zxc.z;
		}
		
	}
}
