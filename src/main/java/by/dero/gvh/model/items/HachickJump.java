package by.dero.gvh.model.items;

import by.dero.gvh.GameObject;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.HachickJumpInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HachickJump extends Item implements DoubleSpaceInterface {
	private final double force;
	private final double radius;
	private final double damage;
	
	public HachickJump(String name, int level, Player owner) {
		super(name, level, owner);
		
		HachickJumpInfo info = (HachickJumpInfo) getInfo();
		force = info.getForce();
		damage = info.getDamage();
		radius = info.getRadius();
	}
	
	@Override
	public void onDoubleSpace() {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		Vector vel = owner.getLocation().getDirection().multiply(force).setY(0.5);
		owner.setVelocity(vel);
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (GameUtils.isDeadPlayer(owner)) {
					this.cancel();
				} else if (owner.getVelocity().getY() == GameUtils.zeroVelocity.getY()) {
					this.cancel();
					Location at = owner.getLocation().add(0, 1, 0);
					at.getWorld().playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
					at.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, at, 1);
					for (GameObject obj : GameUtils.getGameObjects()) {
						if (obj.getTeam() != getTeam() && obj.getEntity().getLocation().distance(at) <= radius) {
							GameUtils.damage(damage, obj.getEntity(), owner);
						}
					}
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
		Game.getInstance().getRunnables().add(runnable);
	}
}
