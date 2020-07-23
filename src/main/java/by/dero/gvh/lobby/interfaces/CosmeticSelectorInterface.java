package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class CosmeticSelectorInterface extends Interface {
	public CosmeticSelectorInterface(InterfaceManager manager, Player player) {
		super(manager, player, (int) Math.ceil((double)Plugin.getInstance().getData().getClassNameToDescription().size() / 7),
				Lang.get("cosmetic.title"));
		
		Collection<UnitClassDescription> classes = Plugin.getInstance().getData().getClassNameToDescription().values();
		
		ItemStack emptySlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
		InterfaceUtils.changeName(emptySlot, Lang.get("interfaces.empty"));
		ItemStack common = GameUtils.getHead(player);
		InterfaceUtils.changeName(common, Lang.get("cosmetic.common"));
		
		ArrayList<ItemStack> classSlots = new ArrayList<>(classes.size());
		for (UnitClassDescription desc : classes) {
			ItemStack head = Heads.getHead(desc.getName());
			InterfaceUtils.changeName(head, "§a" + Lang.get("classes." + desc.getName()));
			classSlots.add(head);
		}
		
		addItem(0, getHeight() - 1, common);
		int x = 2, y = getHeight() - 1;
		for (ItemStack slot : classSlots) {
			addItem(x, y, slot);
			x++;
			if (x == 9) {
				x = 2;
				y--;
			}
		}
	}
}
