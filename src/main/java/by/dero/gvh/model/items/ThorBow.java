package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.ThorBowInfo;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ThorBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	private final double damage;

	public ThorBow (String name, int level, Player owner) {
		super(name, level, owner);
		ThorBowInfo info = (ThorBowInfo) getInfo();
		damage = info.getDamage();
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		if (!cooldown.isReady()) {
			event.getProjectile().remove();
			event.setCancelled(true);
			return;
		}
		cooldown.reload();
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
		SpawnUtils.spawnLightning(at, damage, 2, ownerGP);
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
