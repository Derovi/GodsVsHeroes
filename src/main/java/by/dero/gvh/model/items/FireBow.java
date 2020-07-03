package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

public class FireBow extends Item implements PlayerShootBowInterface {
	public FireBow (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		event.getProjectile().setFireTicks(400);
	}
}
