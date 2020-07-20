package by.dero.gvh.model.items;

import by.dero.gvh.GameObject;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.FireBowInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FireBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	private final double radius;
	private final Material material;

	public FireBow (String name, int level, Player owner) {
		super(name, level, owner);

		FireBowInfo info = (FireBowInfo) getInfo();
		radius = info.getRadius();
		material = info.getMaterial();
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		owner.setCooldown(material, (int) cooldown.getDuration());
		Entity proj = event.getProjectile();
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				if (proj.isDead()) {
					this.cancel();
				}
				proj.getWorld().spawnParticle(Particle.LAVA, proj.getLocation(), 1);
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 5);
		Game.getInstance().getRunnables().add(runnable);
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		Location loc = event.getEntity().getLocation();
		for (GameObject go : GameUtils.getGameObjects()) {
			Location cur = go.getEntity().getLocation();
			if (cur.distance(loc) < radius && go.getTeam() != getTeam()) {
				go.getEntity().setFireTicks(120);
				Location at = cur.toVector().getMidpoint(loc.toVector()).toLocation(loc.getWorld());
				at.getWorld().spawnParticle(Particle.LAVA, at, 1);
			}
		}
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {
	}
}
