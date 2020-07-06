package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ThorBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	public ThorBow (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		Arrow arrow = (Arrow) event.getProjectile();
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				arrow.getWorld().spawnParticle(Particle.LAVA, arrow.getLocation(), 0, 0, 0, 0);
				if (arrow.isDead()) {
					this.cancel();
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		Location at = event.getEntity().getLocation();
		GameUtils.setLastUsedLightning(owner);
		at.getWorld().strikeLightning(at);
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
