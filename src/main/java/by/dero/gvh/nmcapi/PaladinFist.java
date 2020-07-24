package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;

public class PaladinFist extends EntityArmorStand {
	private final ArmorStand stand;
	private final double damage;
	private final Vector dir;
	private final GamePlayer owner;
	
	public long end;
	public PaladinFist(Location loc, ItemStack itemStack, double speed, int lifeTime, double damage, GamePlayer owner) {
		super(((CraftWorld) loc.getWorld()).getHandle());
		this.dir = loc.getDirection().multiply(speed);
		this.stand = (ArmorStand) getBukkitEntity();
		this.setPositionRotation(loc.x, loc.y, loc.z, loc.yaw - 90, loc.pitch);
		this.stand.setRightArmPose(new EulerAngle(270, 0, 0));
		this.stand.setItemInHand(itemStack);
		this.damage = damage;
		this.end = System.currentTimeMillis() + lifeTime * 50;
		this.owner = owner;
		this.stand.setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
		setInvisible(true);
		noclip = true;
		world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
	}
	
	public final void explode(Collection<LivingEntity> enemies) {
		Location loc = stand.getLocation();
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
		if (enemies == null) {
			enemies = loc.getNearbyLivingEntities(2, (e) -> GameUtils.isEnemy(e, owner.getTeam()));
		}
		for (LivingEntity enemy : enemies) {
			GameUtils.damage(damage, enemy, owner.getPlayer());
		}
	}
	
	public Position getItemPosition() {
		double rotX = stand.getLocation().getYaw();
		double rotY = rightArmPose.x;
		Vector vector = new Vector();
		vector.setY(-Math.sin(Math.toRadians(rotY)));
		double xz = Math.cos(Math.toRadians(rotY));
		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
		vector.normalize();
		
		Vector vector2 = new Vector();
		vector2.setY(-Math.sin(Math.toRadians(rotY + 90)));
		double xz2 = Math.cos(Math.toRadians(rotY + 90));
		vector2.setX(-xz2 * Math.sin(Math.toRadians(rotX)));
		vector2.setZ(xz2 * Math.cos(Math.toRadians(rotX)));
		vector2.normalize();
		
		double vx = vector.getX() * 0.5 + vector2.getX() * 0.7;
		double vy = vector.getY() * 0.5 + vector2.getY() * 0.7;
		double vz = vector.getZ() * 0.5 + vector2.getZ() * 0.7;
		
		return new Position(locX - dir.z * 0.45 + vx,
				locY + 1.5 + vy,
				locZ + dir.x * 0.45 + vz);
	
	}
	
	@Override
	public void move(EnumMoveType moveType, double x, double y, double z) {
		super.move(moveType, x, y, z);
		Location at = getItemPosition().toLocation(stand.getWorld());
		at.getWorld().spawnParticle(Particle.LAVA, at, 1);
		if (System.currentTimeMillis() > end) {
			explode(null);
			this.die();
			return;
		}
		
		Collection<LivingEntity> enemies = at.getNearbyLivingEntities(1.5, (e) -> GameUtils.isEnemy(e, owner.getTeam()));
		if (!enemies.isEmpty() || !GameUtils.isVoid(at.getBlock().getType())) {
			explode(enemies);
			this.die();
		} else {
			motX = dir.x;
			motY = dir.y;
			motZ = dir.z;
		}
	}
}
