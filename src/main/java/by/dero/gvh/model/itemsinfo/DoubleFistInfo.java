package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.interfaces.Damaging;

public class DoubleFistInfo extends ItemInfo implements Damaging {
	private int damage;
	
	public DoubleFistInfo(ItemDescription description) {
		super(description);
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
}
