package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import org.bukkit.Material;

public class PaladinArmorInfo extends ItemInfo {
	private int duration;
	private int swordDamage;
	private double speed;
	private Material helmet;
	private Material chestplate;
	private Material leggings;
	private Material boots;
	private Material sword;
	
	public PaladinArmorInfo(ItemDescription description) {
		super(description);
	}
	
	public Material getHelmet() {
		return helmet;
	}
	
	public void setHelmet(Material helmet) {
		this.helmet = helmet;
	}
	
	public Material getChestplate() {
		return chestplate;
	}
	
	public void setChestplate(Material chestplate) {
		this.chestplate = chestplate;
	}
	
	public Material getLeggings() {
		return leggings;
	}
	
	public void setLeggings(Material leggings) {
		this.leggings = leggings;
	}
	
	public Material getBoots() {
		return boots;
	}
	
	public void setBoots(Material boots) {
		this.boots = boots;
	}
	
	public Material getSword() {
		return sword;
	}
	
	public void setSword(Material sword) {
		this.sword = sword;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public int getSwordDamage() {
		return swordDamage;
	}
	
	public void setSwordDamage(int swordDamage) {
		this.swordDamage = swordDamage;
	}
}
