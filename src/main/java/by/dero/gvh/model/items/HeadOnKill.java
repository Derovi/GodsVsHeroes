package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerKillInterface;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HeadOnKill extends Item implements PlayerKillInterface {
	private final float speed = 540.0f;
	public HeadOnKill(String name, int level, Player owner) {
		super(name, level, owner);
	}
	
	@Override
	public void onPlayerKill(Player target) {
		EntityArmorStand stand = new EntityArmorStand(((CraftWorld) target.getWorld()).getHandle(),
				target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
		
		stand.setCustomNameVisible(false);
		stand.setInvisible(true);
		stand.canPickUpLoot = false;
		stand.noclip = true;
		stand.collides = false;
		stand.invulnerable = true;
		((CraftArmorStand) stand.getBukkitEntity()).setHelmet(GameUtils.getHead(target));
//		stand.setEquipment(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(GameUtils.getHead(target)));
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
