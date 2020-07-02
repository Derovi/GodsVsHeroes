package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleportPearls extends Item implements PlayerInteractInterface,
		InfiniteReplenishInterface, ProjectileHitInterface {

	public TeleportPearls (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerInteract (PlayerInteractEvent event) {
		Projectile proj = GameUtils.spawnProjectile(owner.getEyeLocation(), 1.2, EntityType.ENDER_PEARL, owner);
		summonedEntityIds.add(proj.getUniqueId());
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		Location loc = event.getEntity().getLocation();
		owner.teleport(loc);
		int parts = 4;
		for (int i = 0; i < parts; i++) {
			Drawings.drawCircle(loc, 1.5, Particle.SPELL_WITCH);
			loc.add(0, 2.0 / parts, 0);
		}
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
