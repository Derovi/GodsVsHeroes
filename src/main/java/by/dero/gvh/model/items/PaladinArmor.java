package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.PaladinArmorInfo;
import by.dero.gvh.nmcapi.ChasingStand;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.SafeRunnable;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PaladinArmor extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
	private final int duration;
	private final double speed;
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	private ItemStack sword;
	
	public PaladinArmor(String name, int level, Player owner) {
		super(name, level, owner);
		
		PaladinArmorInfo info = (PaladinArmorInfo) getInfo();
		duration = info.getDuration();
		speed = info.getSpeed();
		helmet = new ItemStack(info.getHelmet());
		chestplate = new ItemStack(info.getChestplate());
		leggings = new ItemStack(info.getLeggings());
		boots = new ItemStack(info.getBoots());
		sword = new ItemStack(info.getSword());
		
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(sword);
		NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = new NBTTagList();
		NBTTagCompound damage = new NBTTagCompound();
		damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
		damage.set("Name", new NBTTagString("generic.attackDamage"));
		damage.set("Amount", new NBTTagInt(info.getSwordDamage()));
		damage.set("Operation", new NBTTagInt(0));
		damage.set("UUIDLeast", new NBTTagInt(894654));
		damage.set("UUIDMost", new NBTTagInt(2872));
		damage.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(damage);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		sword = CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		
		for (int type = -4; type <= 0; type++) {
			Location oLoc = owner.getLocation();
			Vector dir = oLoc.getDirection();
			Location at = oLoc.clone().add(dir.x * 3, 0, dir.z * 3);
			at.add(MathUtils.getRightVector(dir).multiply(Math.random() * 5 - 2.5));
			
			World world = ((CraftWorld) owner.getWorld()).world;
			ChasingStand handle = new ChasingStand(world, at.x, at.y, at.z, speed, 0.5, owner);
			
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			handle.setNoGravity(false);
			
			EntityEquipment eq = stand.equipment;
			ItemStack item;
			switch (type) {
				case -1 : eq.setHelmet(helmet); item = helmet; break;
				case -2 : eq.setChestplate(chestplate); item = chestplate; break;
				case -3 : eq.setLeggings(leggings); item = leggings; break;
				case -4 : eq.setBoots(boots); item = boots; break;
				default : eq.setItemInMainHand(sword); item = sword; break;
			}
			
			int finalType = type;
			handle.onReach = new SafeRunnable() {
				@Override
				public void run() {
					GameUtils.changeEquipment(owner, finalType, duration, item);
				}
			};
			world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
		}
	}
}
