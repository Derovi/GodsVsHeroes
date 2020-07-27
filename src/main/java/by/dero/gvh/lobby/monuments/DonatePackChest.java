package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DonatePackChest {
	@Getter @Setter
	private static int animCnt = 0;
	
	private final int animDuration = 200;
	private final double radius = 1.5;
	private Location center;
	private final double part = MathUtils.PI2 / 5;
	
	private EntityFallingBlock chest;
	
	public Location getInCircle(int pt) {
		return center.clone().add(MathUtils.cos(part * pt + Math.PI / 2) * radius, MathUtils.sin(part * pt + Math.PI / 2) * radius, 0);
	}
	
	public DonatePackChest(DirectedPosition position) {
		Location loc = position.toLocation(Lobby.getInstance().getWorld());
		System.out.println(loc.toVector().toString());
		MaterialData data = new MaterialData(Material.CHEST);

		new BukkitRunnable() {
			@Override
			public void run() {

				chest = new EntityFallingBlock(((CraftWorld)loc.getWorld()).getHandle(),
						loc.x, loc.y, loc.z, CraftMagicNumbers.getBlock(Material.STONE).fromLegacyData(0));

				chest.setNoGravity(true);
				chest.noclip = true;
				chest.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
				((CraftWorld)loc.getWorld()).getHandle().addEntity(chest, CreatureSpawnEvent.SpawnReason.CUSTOM);
			}
		}.runTaskLater(Plugin.getInstance(), 40);


		/*center = loc.clone().subtract(MathUtils.cos(part * 3 + Math.PI / 2) * radius, MathUtils.sin(part * 3 + Math.PI / 2) * radius, 0);

		BukkitRunnable runnable = new BukkitRunnable() {
			int animTime = 0;
			int pt = 3;
			final double partSpeed = 5.0 / animDuration;
			Vector delta = getInCircle(0).toVector().subtract(getInCircle(3).toVector()).multiply(partSpeed);
			@Override
			public void run() {
				if (animCnt > 0) {
					animTime++;
					chest.locX += delta.x;
					chest.locY += delta.y;
					chest.locZ += delta.z;
//					chest.motX = delta.x / delta.length() * 0.05;
//					chest.motY = delta.y / delta.length() * 0.05;
//					chest.motZ = delta.z / delta.length() * 0.05;
					chest.getBukkitEntity().getLocation().getWorld().spawnParticle(Particle.FLAME,
							chest.getBukkitEntity().getLocation(), 0, 0, 0, 0);
					
					if (animTime % (animDuration / 5) == 0) {
						pt = (pt + 2) % 5;
						delta = getInCircle((pt + 2) % 5).toVector().subtract(getInCircle(pt).toVector()).multiply(partSpeed);
					}
					if (animTime == animDuration) {
						animTime = 0;
						animCnt--;
					}
				}
				//System.out.println(chest.getBukkitEntity().getLocation().toVector().toString());
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);*/
	}
}
