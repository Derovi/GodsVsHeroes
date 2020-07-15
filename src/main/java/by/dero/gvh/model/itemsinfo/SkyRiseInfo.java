package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class SkyRiseInfo extends ItemInfo {
	private double radius;
	private double damage;
	private int explosions;

	public SkyRiseInfo(ItemDescription description) {
		super(description);
	}

	public int getExplosions () {
		return explosions;
	}

	public void setExplosions (int explosions) {
		this.explosions = explosions;
	}

	public double getRadius () {
		return radius;
	}

	public void setRadius (double radius) {
		this.radius = radius;
	}

	public double getDamage () {
		return damage;
	}

	public void setDamage (double damage) {
		this.damage = damage;
	}
}
