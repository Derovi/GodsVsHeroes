package by.dero.gvh.nmcapi.throwing;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Position;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ThrowingHead extends EntityArmorStand {
	private final ArmorStand armorStand;
	private Entity owner = null;
	private boolean stopped = false;
	private boolean removed = false;
	@Setter private int liveTimeAfterStop = 0;
	private double xDir;
	private double zDir;
	@Setter private double spinning = 0;
	private double step = 0.40;
	private Runnable onHitBlock = null;
	private Runnable onDisappear = null;
	private Runnable onOwnerPickUp = null;
	@Setter private boolean physicsSpin = false;
	
	public ThrowingHead(Location loc, ItemStack itemStack) {
		super(((CraftWorld) loc.getWorld()).getHandle());
		armorStand = (ArmorStand) getBukkitEntity();
		xDir = loc.getDirection().getX();
		zDir = loc.getDirection().getZ();
		double lenDir = Math.sqrt(xDir * xDir + zDir * zDir);
		xDir /= lenDir;
		zDir /= lenDir;
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		this.setYawPitch(loc.getYaw(), loc.getPitch());
		this.setInvisible(true);
		this.setInvulnerable(true);
		this.noclip = true;
		setItem(itemStack);
	}
	
	public void setItem(ItemStack item) {
		armorStand.setHelmet(item);
	}
	
	public void setVelocity(Vector vector) {
		armorStand.setVelocity(vector);
	}
	
	public Position getItemPosition() {
		return new Position(locX, locY + armorStand.getHeight(), locZ);
	}
	
	private void stop() {
		if (stopped)  {
			return;
		}
		stopped = true;
		setMarker(true);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (removed) {
					return;
				}
				remove();
				if (onDisappear != null) {
					onDisappear.run();
				}
			}
		}.runTaskLater(Plugin.getInstance(), liveTimeAfterStop);
	}
	
	public void remove() {
		removed = true;
		if (!armorStand.isDead()) {
			armorStand.remove();
		}
	}
	
	@Override
	public void move(EnumMoveType moveType, double x, double y, double z) {
		if (stopped) {
			if (onOwnerPickUp != null) {
				Collection<Entity> entities = armorStand.getLocation().getWorld().getNearbyEntities(
						armorStand.getLocation(), 1.25, 1.25, 1.25);
				for (Entity entity : entities) {
					if (entity.getUniqueId().equals(owner.getUniqueId()) || entity.getPassengers().contains(owner)) {
						onOwnerPickUp.run();
						break;
					}
				}
			}
			return;
		}
		
		Vector vector = new Vector(x, y, z);
		double length = vector.length();
		
		int stepCount = (int) (length / step) + 1;
		
		int step;
		for (step = 0; step < stepCount; ++ step) {
			locX += x / stepCount;
			locY += y / stepCount;
			locZ += z / stepCount;
			//super.move(moveType, x, y, z);
			Location itemLocation = getItemPosition().toLocation(armorStand.getWorld());
			if (itemLocation.getY() < 0) {
				stop();
				break;
			}
			if (!GameUtils.isVoid(armorStand.getLocation().getWorld().getBlockAt(itemLocation).getType())) {
				stop();
				if (onHitBlock != null) {
					onHitBlock.run();
				}
				break;
			}
		}
		
		if (!physicsSpin) {
			setHeadPose(new Vector3f((float) (headPose.x + spinning * length / stepCount * step), headPose.y, headPose.z));
		} else {
			setHeadPose(new Vector3f(360f - (float) Math.toDegrees(Math.asin(y / length)),headPose.y,headPose.z));
		}
	}
	
	public void spawn() {
		((CraftWorld) armorStand.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
	}
}
