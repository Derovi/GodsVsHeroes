package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.LightningBowInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
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
	private final Material material;

	public LightningBow (String name, int level, Player owner) {
		super(name, level, owner);

		LightningBowInfo info = (LightningBowInfo) getInfo();
		radius = info.getRadius();
		damage = info.getDamage();
		material = info.getMaterial();
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		if (!cooldown.isReady()) {
			summonedEntityIds.remove(event.getProjectile().getUniqueId());
			event.setCancelled(true);
			return;
		}
		cooldown.reload();
		owner.setCooldown(material, (int) cooldown.getDuration());
		Arrow arrow = (Arrow) event.getProjectile();

		summonedEntityIds.add(arrow.getUniqueId());
		BukkitRunnable runnable = new BukkitRunnable() {
			final HashSet<UUID> hit = new HashSet<>();
			@Override
			public void run () {
				if (!summonedEntityIds.contains(arrow.getUniqueId())) {
					this.cancel();
					return;
				}

				for (Entity entity : arrow.getNearbyEntities(radius, radius+3, radius)) {
					if (GameUtils.isEnemy(entity, getTeam())) {
						LivingEntity liv = (LivingEntity) entity;
						if (liv.getEyeLocation().distance(arrow.getLocation()) <= radius && !hit.contains(liv.getUniqueId())) {
							hit.add(liv.getUniqueId());

							Drawings.drawLineColor(arrow.getLocation(), liv.getEyeLocation(), 255, 0, 0);
							liv.getLocation().getWorld().playSound(liv.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.8f, 1);
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
		summonedEntityIds.remove(event.getEntity().getUniqueId());
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
