package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.Interface;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class BoosterStand {
	@Getter private final Location location;
	
	@Getter private final ArmorStand stand;
	
	private final Class<? extends Interface> inter;
	
	public BoosterStand(DirectedPosition position, String name, String headName, Class<? extends Interface> inter) {
		this.inter = inter;
		location  = position.toLocation(Lobby.getInstance().getWorld());
		stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		GameUtils.setInvisibleFlags(stand);
		stand.setCustomNameVisible(true);
		stand.setCustomName(name);
		
		stand.setHelmet(Heads.getHead(headName));
		new BukkitRunnable() {
			double add = Math.PI / 3 / 40;
			double angle = 0;
			@Override
			public void run() {
				stand.setHeadPose(new EulerAngle(0, 0, angle));
				Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 0.5, Math.random() * MathUtils.PI2,
						Math.random() * MathUtils.PI2);
				for (int i = 0; i < 3; i++) {
					stand.getWorld().spawnParticle(Particle.SPELL, stand.getEyeLocation().add(0, -1, 0), 0, at.x, at.y+1, at.z);
				}
				
				at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1.5, Math.random() * MathUtils.PI2,
						Math.random() * MathUtils.PI2);
				for (int i = 0; i < 3; i++) {
					stand.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, stand.getEyeLocation().add(0, 1, 0), 0,
							at.x, Math.abs(at.y), at.z);
				}
				
				if (angle > Math.PI / 6 || angle < -Math.PI / 6) {
					add *= -1;
				}
				angle += add;
			}
		}.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
	
	public void onClick(Player player) {
		try {
			inter.getConstructor(Player.class).newInstance(player).open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unload() {
		stand.remove();
	}
}
