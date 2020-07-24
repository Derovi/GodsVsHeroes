package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.StaticCustomization;
import by.dero.gvh.model.interfaces.Damaging;
import by.dero.gvh.nmcapi.NMCUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.inventory.ItemStack;

public class DoubleFistInfo extends ItemInfo implements Damaging {
	private int damage;

	@StaticCustomization
	public static void staticCustomize(ItemStack itemStack, ItemInfo info) {
		NBTTagCompound nbt = NMCUtils.getNBT(itemStack);
		nbt.set("ether", new NBTTagString("glove"));
		NMCUtils.setNBT(itemStack, nbt);
	}
	
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
