package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleportPearls extends Item implements PlayerInteractInterface,
		InfiniteReplenishInterface, ProjectileHitInterface {

	private final Material material;
	public TeleportPearls (String name, int level, Player owner) {
		super(name, level, owner);
		
		material = getInfo().getMaterial();
	}

	@Override
	public void onPlayerInteract (PlayerInteractEvent event) {
		if (ownerGP.isCharged(getName())) {
			owner.setCooldown(material, (int) cooldown.getDuration());
		}
		Projectile proj = SpawnUtils.spawnProjectile(owner.getEyeLocation(), 1.2, EntityType.SNOWBALL, owner);
		summonedEntityIds.add(proj.getUniqueId());
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		event.getEntity().remove();
		Location loc = event.getEntity().getLocation();
		owner.getWorld().playSound(loc, Sound.ENTITY_ENDERMEN_TELEPORT, 1.07f, 1);
		GameUtils.damage(5, owner, owner);
		owner.teleport(new Location(loc.world, loc.x, loc.y, loc.z, owner.getLocation().yaw, owner.getLocation().pitch));
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
