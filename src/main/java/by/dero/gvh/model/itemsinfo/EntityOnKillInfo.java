package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import org.bukkit.entity.EntityType;

public class EntityOnKillInfo extends ItemInfo {
	private EntityType type;

	public EntityOnKillInfo(ItemDescription description) {
		super(description);
	}

	public EntityType getType () {
		return type;
	}

	public void setType (EntityType type) {
		this.type = type;
	}
}
