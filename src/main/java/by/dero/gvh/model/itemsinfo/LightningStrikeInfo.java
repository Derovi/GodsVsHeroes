package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class LightningStrikeInfo extends ItemInfo {
	private double radius;
	private double damage;
	private double lightningDamage;
	
	public LightningStrikeInfo(ItemDescription description) {
		super(description);
	}
	
	public double getDamage() {
		return damage;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public double getLightningDamage() {
		return lightningDamage;
	}
	
	public void setLightningDamage(double lightningDamage) {
		this.lightningDamage = lightningDamage;
	}
}