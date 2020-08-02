package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class CosmeticsUtils {
	public static void spawnGrave(Player target, Player killer) {
		if (!Plugin.getInstance().getCosmeticManager().isEnabled(killer, "grave")) {
			return;
		}
		Vector[] offset = {
				new Vector(-0.15, 0, 0.15),
				new Vector(0.15, 0, 0.15),
				new Vector(0.15, 0, -0.15),
				new Vector(-0.15, 0, -0.15)
		};
		
		World world = ((CraftPlayer) target).getHandle().world;
		EntityArmorStand handle;
		CraftArmorStand stand;
		Vector at;
		EntityArmorStand[] ent = new EntityArmorStand[6];
		for (int i = 0; i < 4; i++) {
			handle = new EntityArmorStand(world);
			handle.setSmall(true);
			at = target.getLocation().toVector().add(offset[i]);
			handle.setPosition(at.x, at.y - handle.getHeadHeight() + 0.1, at.z);
			
			stand = (CraftArmorStand) handle.getBukkitEntity();
			stand.setHelmet(new ItemStack(Material.STONE));
			switch (i) {
				case 0 : stand.setHeadPose(new EulerAngle(Math.PI / 4, 0, -Math.PI / 4)); break;
				case 1 : stand.setHeadPose(new EulerAngle(Math.PI / 4, 0, Math.PI / 4)); break;
				case 2 : stand.setHeadPose(new EulerAngle(-Math.PI / 4, 0, Math.PI / 4)); break;
				case 3 : stand.setHeadPose(new EulerAngle(-Math.PI / 4, 0, -Math.PI / 4)); break;
			}
			GameUtils.setInvisibleFlags(stand);
			stand.setMarker(true);
			world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			ent[i] = handle;
		}
		
		handle = new EntityArmorStand(world);
		at = target.getLocation().toVector().add(new Vector(-0.555, 0, 0.25));
		handle.setPosition(at.x, at.y, at.z);
		stand = (CraftArmorStand) handle.getBukkitEntity();
		stand.setHeadPose(new EulerAngle(0, 0, Math.PI / 4 * 3));
		stand.setHelmet(new ItemStack(Material.IRON_SWORD));
		stand.setMarker(true);
		GameUtils.setInvisibleFlags(stand);
		world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
		ent[4] = handle;
		
		
		handle = new EntityArmorStand(world);
		at = target.getLocation().toVector().add(new Vector(0, 0.5, 0));
		handle.setPosition(at.x, at.y, at.z);
		handle.setSmall(true);
		stand = (CraftArmorStand) handle.getBukkitEntity();
		stand.setHelmet(GameUtils.getHead(target));
		stand.setMarker(true);
		GameUtils.setInvisibleFlags(stand);
		world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
		ent[5] = handle;
		
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			for (EntityArmorStand e : ent) {
				e.die();
			}
		}, Game.getInstance().getInfo().getRespawnTime());
	}
	
	public static void dropHead(Player target, Player killer) {
		if (!Plugin.getInstance().getCosmeticManager().isEnabled(killer, "headDrop")) {
			return;
		}
		float speed = 540.0f;
		Location loc = target.getLocation();
		
		EntityArmorStand stand = new EntityArmorStand(((CraftWorld) target.getWorld()).getHandle());

		stand.setPositionRotation(loc.x, loc.y, loc.z, (killer.getLocation().yaw + 180) % 360, 90);
		stand.setCustomNameVisible(false);
		stand.setInvisible(true);
		stand.canPickUpLoot = false;
		stand.noclip = true;
		stand.collides = false;
		stand.invulnerable = true;

		CraftArmorStand craftStand = (CraftArmorStand) stand.getBukkitEntity();
		craftStand.setHelmet(GameUtils.getHead(target));
		target.getWorld().spawnParticle(Particle.BLOCK_CRACK, target.getEyeLocation(), 20,
				new MaterialData(Material.REDSTONE_BLOCK));
		Vector vel = target.getLocation().subtract(killer.getLocation()).toVector().normalize().multiply(0.7).setY(0.9);
		craftStand.setVelocity(vel);
		stand.setHeadPose(new Vector3f(loc.pitch, loc.yaw - stand.yaw, 0));
		
		stand.world.addEntity(stand, CreatureSpawnEvent.SpawnReason.CUSTOM);
		BukkitRunnable runnable = new BukkitRunnable() {
			int ticks = -1;
			@Override
			public void run() {
				if (ticks == -1 && !craftStand.getEyeLocation().add(0, -0.5, 0).getBlock().getType().equals(Material.AIR)) {
					ticks = 0;
					stand.setNoGravity(true);
				}
				if (ticks != -1) {
					ticks++;
				} else {
					stand.setHeadPose(new Vector3f(stand.headPose.x, stand.headPose.y + speed / 20, stand.headPose.z));
				}
				if (ticks > 80) {
					stand.die();
					this.cancel();
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
		Game.getInstance().getRunnables().add(runnable);
	}
}
