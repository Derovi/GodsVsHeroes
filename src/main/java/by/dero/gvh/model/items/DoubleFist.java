package by.dero.gvh.model.items;

import by.dero.gvh.Cooldown;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.DynamicCustomization;
import by.dero.gvh.model.interfaces.DoubleHandInteractInterface;
import by.dero.gvh.model.interfaces.DoubleHanded;
import by.dero.gvh.model.itemsinfo.DoubleFistInfo;
import by.dero.gvh.nmcapi.NMCUtils;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DoubleFist extends Item implements DoubleHanded, DoubleHandInteractInterface {
	private Runnable onHit = null;
	private final Cooldown cooldownOffhand;
	private int damage;
	private double range = 4;
	private long lastUsed;
	private boolean red = false;

	public DoubleFist(String name, int level, Player owner) {
		super(name, level, owner);
		cooldownOffhand = new Cooldown(this.cooldown.getDuration());

		DoubleFistInfo info = (DoubleFistInfo) getInfo();
		damage = info.getDamage();
	}

	@DynamicCustomization
	public void customize(ItemStack itemStack, ItemInfo info, Player player) {
		NBTTagCompound nbt = NMCUtils.getNBT(itemStack);
		nbt.set("ether", new NBTTagString("glove"));
		NMCUtils.setNBT(itemStack, nbt);
	}

	public ItemStack getItemStack(boolean rightHand) {
		ItemStack itemStack = getItemStack();
		NBTTagCompound nbt = NMCUtils.getNBT(itemStack);
		if (rightHand) {
			if (!cooldown.isReady()) {
				nbt.set("ether", new NBTTagString("glove_white"));
			} else {
				if (red) {
					nbt.set("ether", new NBTTagString("glove_red"));
				} else {
					nbt.set("ether", new NBTTagString("glove"));
				}
			}
		} else {
			if (!cooldownOffhand.isReady()) {
				nbt.set("ether", new NBTTagString("glove_white"));
			} else {
				if (red) {
					nbt.set("ether", new NBTTagString("glove_red"));
				} else {
					nbt.set("ether", new NBTTagString("glove"));
				}
			}
		}
		NMCUtils.setNBT(itemStack, nbt);
		return itemStack;
	}
	
	@Override
	public void interactMainHand(PlayerInteractEvent event) {
		if (!cooldown.isReady() || System.currentTimeMillis() - lastUsed < 100) {
			return;
		}
		cooldown.reload();
		lastUsed = System.currentTimeMillis();
		if (onHit != null) {
			onHit.run();
		}
		LivingEntity ent = GameUtils.getTargetEntity(owner, range, (e) -> GameUtils.isEnemy(e, getTeam()));
		if (ent != null) {
			GameUtils.damage(damage, ent, owner);
		}
	}
	
	@Override
	public void interactOffHand(PlayerInteractEvent event) {
		if (!cooldownOffhand.isReady() || System.currentTimeMillis() - lastUsed < 100) {
			return;
		}
		cooldownOffhand.reload();
		lastUsed = System.currentTimeMillis();
		if (onHit != null) {
			onHit.run();
		}
		LivingEntity ent = GameUtils.getTargetEntity(owner, 3.3, (e) -> GameUtils.isEnemy(e, getTeam()));
		if (ent != null) {
			GameUtils.damage(damage, ent, owner);
		}
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public Runnable getOnHit() {
		return onHit;
	}
	
	public void setOnHit(Runnable onHit) {
		this.onHit = onHit;
	}
	
	public double getRange() {
		return range;
	}
	
	public void setRange(double range) {
		this.range = range;
	}

	public boolean isRed() {
		return red;
	}

	public void setRed(boolean red) {
		this.red = red;
	}
}
