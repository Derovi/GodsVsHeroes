package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;

public class CosmeticsUtils {
	public static void dropHead(Player target, Player killer) {
		if (!Plugin.getInstance().getCosmeticManager().isEnabled(killer, "headDrop")) {
			return;
		}
//		float speed = 540.0f;
//		Location loc = target.getLocation();
//
//		ThrowingHead head = new ThrowingHead(loc, GameUtils.getHead(target));
//		head.setPhysicsSpin(true);
//		head.setHeadPose(new Vector3f(target.getLocation().getPitch(), 0, 0));
//		head.setLiveTimeAfterStop(80);
//		head.setVelocity(killer.getLocation().getDirection().multiply(0.5).setY(1));
//		head.spawn();
//
//		EntityArmorStand stand = new EntityArmorStand(((CraftWorld) target.getWorld()).getHandle());

//		stand.setPositionRotation(loc.x, loc.y, loc.z, (killer.getLocation().yaw + 180) % 360, 90);
//		stand.setPositionRotation(loc.x, loc.y, loc.z, (killer.getLocation().yaw + 180) % 360, 90);
//		stand.setCustomNameVisible(false);
////		stand.setInvisible(true);
//		stand.canPickUpLoot = false;
//		stand.noclip = true;
//		stand.collides = false;
//		stand.invulnerable = true;
//
//		CraftArmorStand craftStand = (CraftArmorStand) stand.getBukkitEntity();
//		craftStand.setHelmet(GameUtils.getHead(target));
//		target.getWorld().spawnParticle(Particle.BLOCK_CRACK, target.getEyeLocation(), 20,
//				new MaterialData(Material.REDSTONE_BLOCK));
//		Vector vel = target.getLocation().subtract(killer.getLocation()).toVector().normalize().multiply(0.7).setY(0.9);
//		craftStand.setVelocity(vel);
//		stand.setHeadPose(new Vector3f(loc.pitch, loc.yaw - stand.yaw, 0));
		
//		EntityArmorStand zxc = new EntityArmorStand(stand.world);
//		zxc.setPositionRotation(stand.locX, stand.locY, stand.locZ, stand.yaw, stand.pitch);
//		zxc.setHeadPose(stand.headPose);
//		((CraftArmorStand) zxc.getBukkitEntity()).setHelmet(craftStand.getHelmet());
//		zxc.world.addEntity(zxc, CreatureSpawnEvent.SpawnReason.CUSTOM);
		
//		stand.world.addEntity(stand, CreatureSpawnEvent.SpawnReason.CUSTOM);
//		BukkitRunnable runnable = new BukkitRunnable() {
//			int ticks = 0;
//			@Override
//			public void run() {
//				ticks++;
//				stand.pitch += speed / 20;
//				killer.sendMessage(stand.yaw + " " + stand.pitch);
////				stand.setHeadPose(new Vector3f(stand.headPose.x + speed / 20, stand.headPose.y, stand.headPose.z));
////				killer.sendMessage(stand.headPose.x + " " + stand.headPose.y + " " + stand.headPose.z);
//				if (ticks > 60) {
//					stand.die();
//					this.cancel();
//				}
//			}
//		};
//		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
//		Game.getInstance().getRunnables().add(runnable);
	}
}
