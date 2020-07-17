package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class ThorBowInfo extends ItemInfo {
	private double damage;

	public ThorBowInfo(ItemDescription description) {
		super(description);
	}

	public double getDamage () {
		return damage;
	}

	public void setDamage (double damage) {
		this.damage = damage;
	}
}
