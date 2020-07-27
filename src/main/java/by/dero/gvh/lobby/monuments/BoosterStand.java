package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.Interface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class BoosterStand {
	@Getter private final Location location;
	
	@Getter private final CraftArmorStand stand;
	
	private final Class<? extends Interface> inter;
	
	@Getter @Setter
	private int animCnt = 0;
	
	public BoosterStand(DirectedPosition position, String name, String headName, Class<? extends Interface> inter) {
		this.inter = inter;
		location  = position.toLocation(Lobby.getInstance().getWorld());
		stand = (CraftArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		GameUtils.setInvisibleFlags(stand);
		stand.setCustomNameVisible(true);
		stand.setCustomName(name);
		
		stand.setHelmet(Heads.getHead(headName));
		new BukkitRunnable() {
			int animTime = 0;
			final double yawAccel = 0.006;
			final int animDuration = 160;
			final int stayDuration = (int) (Math.PI * 2 * 8 / yawAccel / animDuration * 2);
			int stay = -1;
			final double dHei = 1.6 / animDuration * 2;
			double add = Math.PI / 3 / 80;
			double roll = 0;
			double yaw = 0, yawSpeed = 0;
			@Override
			public void run() {
				if (animCnt > 0) {
					if (stay >= 0) {
						yaw += yawSpeed;
						stand.setHeadPose(new EulerAngle(0, yaw, 0));
						stay++;
						if (stay == stayDuration) {
							stay = -1;
						}
					} else if (animTime < animDuration / 2) {
						stand.getHandle().locY += dHei;
						stand.setHeadPose(new EulerAngle(0, yaw, 0));
						yawSpeed += yawAccel;
						yaw += yawSpeed;
						if (animTime == animDuration / 2 - 1) {
							stay = 0;
						}
						animTime++;
					} else {
						yaw -= yawSpeed;
						yawSpeed -= yawAccel;
						stand.getHandle().locY -= dHei;
						stand.setHeadPose(new EulerAngle(0, yaw, 0));
						animTime++;
					}
					
					if (animTime == animDuration) {
						yaw = 0;
						animTime = 0;
						animCnt--;
						Drawings.spawnFirework(stand.getEyeLocation(), 1);
					}
				} else {
					stand.setHeadPose(new EulerAngle(0, 0, roll));
					roll += add;
					Vector at;
					for (int i = 0; i < 1; i++) {
						at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 0.5,
								Math.random() * MathUtils.PI2, Math.random() * MathUtils.PI2);
						at.y = Math.abs(at.y);
						at.add(stand.getEyeLocation().add(0, -1.5, 0).toVector());
						stand.getWorld().spawnParticle(Particle.SPELL, at.toLocation(stand.getWorld()), 0, 0, 0.2, 0);
					}
					
					at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1.5, Math.random() * MathUtils.PI2,
							Math.random() * MathUtils.PI2);
					for (int i = 0; i < 3; i++) {
						stand.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, stand.getEyeLocation().add(0, 1, 0), 0,
								at.x, Math.abs(at.y), at.z);
					}
					
					if (roll > Math.PI / 12 || roll < -Math.PI / 12) {
						add *= -1;
					}
				}
			}
		}.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
	
	public void onClick(Player player) {
		try {
			inter.getConstructor(InterfaceManager.class, Player.class).
					newInstance(Lobby.getInstance().getInterfaceManager(), player).open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unload() {
		stand.remove();
	}
}
