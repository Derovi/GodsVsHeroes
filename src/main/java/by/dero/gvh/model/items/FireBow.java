package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class FireBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	public FireBow (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		event.getProjectile().setFireTicks(400);
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {

	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {
		if (GameUtils.isEnemy(event.getHitEntity(), getTeam())) {
			GameUtils.damage(5, (LivingEntity) event.getHitEntity(), owner);
		}
	}
}
