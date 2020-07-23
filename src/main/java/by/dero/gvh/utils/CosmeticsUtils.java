package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CosmeticsUtils {
	public static void dropHead(Player target) {
		float speed = 540.0f;
		EntityArmorStand stand = new EntityArmorStand(((CraftWorld) target.getWorld()).getHandle(),
				target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
		
		stand.setCustomNameVisible(false);
		stand.setInvisible(true);
		stand.canPickUpLoot = false;
		stand.noclip = true;
		stand.collides = false;
		stand.invulnerable = true;
		((CraftArmorStand) stand.getBukkitEntity()).setHelmet(GameUtils.getHead(target));
		stand.setHeadRotation(0);
		stand.world.addEntity(stand, CreatureSpawnEvent.SpawnReason.CUSTOM);
		BukkitRunnable runnable = new BukkitRunnable() {
			int ticks = 0;
			@Override
			public void run() {
				ticks++;
				stand.motY = -0.06;
				stand.yaw = speed * ticks / 20;
				if (ticks > 60) {
					stand.die();
					this.cancel();
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
		Game.getInstance().getRunnables().add(runnable);
	}
}
