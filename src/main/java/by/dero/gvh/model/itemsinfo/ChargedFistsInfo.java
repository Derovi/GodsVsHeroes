package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class ChargedFistsInfo extends ItemInfo {
	private int damage;
	private int duration;
	private double force;
	private double forceDistance;
	
	public ChargedFistsInfo(ItemDescription description) {
		super(description);
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public double getForce() {
		return force;
	}
	
	public void setForce(double force) {
		this.force = force;
	}
	
	public double getForceDistance() {
		return forceDistance;
	}
	
	public void setForceDistance(double forceDistance) {
		this.forceDistance = forceDistance;
	}
}
