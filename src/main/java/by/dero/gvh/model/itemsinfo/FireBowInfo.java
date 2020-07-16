package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class FireBowInfo extends ItemInfo {
	private double radius;

	public FireBowInfo(ItemDescription description) {
		super(description);
	}

	public double getRadius () {
		return radius;
	}

	public void setRadius (double radius) {
		this.radius = radius;
	}
}