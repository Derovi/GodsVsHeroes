package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
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
		for (double angle = 0; angle < MathUtils.PI2; angle += MathUtils.PI2 / parts) {
			Location t = loc.clone().add(MathUtils.cos(angle), 0, MathUtils.sin(angle));
			Drawings.drawLine(t, t.clone().add(0, 2, 0), Particle.SPELL_WITCH);
		}
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}