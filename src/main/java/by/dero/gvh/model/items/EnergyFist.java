package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.EnergyFistInfo;
import by.dero.gvh.nmcapi.PaladinFist;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnergyFist extends Item implements PlayerInteractInterface {
	private final double damage;
	private final int duration;
	private final double speed;
	private final Material material;
	
	public EnergyFist(String name, int level, Player owner) {
		super(name, level, owner);
		
		EnergyFistInfo info = (EnergyFistInfo) getInfo();
		damage = info.getDamage();
		duration = info.getDuration();
		speed = info.getSpeed();
		material = info.getMaterial();
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		owner.setCooldown(material, (int) cooldown.getDuration());
		
		new PaladinFist(owner.getLocation().add(owner.getLocation().getDirection().multiply(2)),
				getItemStack(), speed, duration, damage, ownerGP);
	}
}
