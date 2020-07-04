package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.LightningBowInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class LightningBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	private final double radius;
	private final double damage;

	public LightningBow (String name, int level, Player owner) {
		super(name, level, owner);

		LightningBowInfo info = (LightningBowInfo) getInfo();
		radius = info.getRadius();
		damage = info.getDamage();
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		Arrow arrow = (Arrow) event.getProjectile();

		BukkitRunnable runnable = new BukkitRunnable() {
			final HashSet<UUID> hit = new HashSet<>();
			@Override
			public void run () {
				if (!Minigame.getInstance().getGameEvents().getProjectiles().contains(arrow.getUniqueId())) {
					this.cancel();
					return;
				}

				for (Entity entity : arrow.getNearbyEntities(radius, radius+3, radius)) {
					if (entity instanceof LivingEntity) {
						LivingEntity liv = (LivingEntity) entity;
						if (liv.getEyeLocation().distance(arrow.getLocation()) <= radius &&
							!hit.contains(liv.getUniqueId()) && GameUtils.isEnemy(liv, getTeam())) {
							hit.add(liv.getUniqueId());

							Drawings.drawLine(arrow.getLocation(), liv.getEyeLocation(), Particle.END_ROD);
							GameUtils.damage(damage, liv, owner);
						}
					}
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
		Game.getInstance().getRunnables().add(runnable);
	}


	@Override
	public void onProjectileHit (ProjectileHitEvent event) {

	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
