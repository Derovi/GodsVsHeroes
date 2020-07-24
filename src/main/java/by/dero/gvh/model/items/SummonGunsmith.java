package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SummonGunsmithInfo;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SummonGunsmith extends Item implements PlayerInteractInterface {
	private final int duration;
	private final int health;
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	private final ItemStack sword;
	private final Material material;
	
	public SummonGunsmith(String name, int level, Player owner) {
		super(name, level, owner);
		
		SummonGunsmithInfo info = (SummonGunsmithInfo) getInfo();
		duration = info.getDuration();
		health = info.getHealth();
		
		helmet = new ItemStack(Material.IRON_HELMET);
		chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		leggings = new ItemStack(Material.IRON_LEGGINGS);
		boots = new ItemStack(Material.IRON_BOOTS);
		sword = new ItemStack(Material.DIAMOND_SWORD);
		material = info.getMaterial();
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		owner.setCooldown(material, (int) cooldown.getDuration());
		
		Location at = owner.getLocation();
		Zombie zombie = (Zombie) SpawnUtils.spawnAIZombie(at, health, 0, duration, false, ownerGP).getEntity();
		zombie.getEquipment().setHelmet(helmet);
		zombie.getEquipment().setChestplate(chestplate);
		zombie.getEquipment().setLeggings(leggings);
		zombie.getEquipment().setBoots(boots);
		zombie.getEquipment().setItemInMainHand(sword);
	}
}
