package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class LightningBowInfo extends ItemInfo {
	private double radius;
	private double damage;

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
