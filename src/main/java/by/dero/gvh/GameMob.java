package by.dero.gvh;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GameMob implements GameObject {
	private final int team;
	private final Player owner;
	private final LivingEntity entity;

	public GameMob(LivingEntity entity, int team, Player owner) {
		this.entity = entity;
		this.team = team;
		this.owner = owner;
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
