package by.dero.gvh;

import by.dero.gvh.model.Lang;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GameMob extends GameObject {
	private final int team;
	private final Player owner;

	public GameMob(LivingEntity entity, int team, Player owner) {
		super(entity);
		this.team = team;
		this.owner = owner;
	}

	public void updateName() {
		entity.setCustomNameVisible(true);
		entity.setCustomName(Lang.get("commands." + (char)('1' + team)).substring(0, 2) + (int)entity.getHealth() + " ❤");
	}

	public int getTeam () {
		return team;
	}

	public Player getOwner () {
		return owner;
	}

	public LivingEntity getEntity () {
		return entity;
	}
}
