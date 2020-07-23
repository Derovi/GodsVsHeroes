package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyCosmeticInterface extends Interface {
	public BuyCosmeticInterface(InterfaceManager manager, Player player, String cosmeticName) {
		super(manager, player, 6, " ");
		
		String[] pattern = {
				"EEEEIEEEE",
				"RREEEEEVV",
				"RREGGGEVV",
				"EEEGGGEEE",
				"EEEGGGEEE",
				"EEEEEEEEE",
		};
		
		ItemStack buyItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
		InterfaceUtils.changeName(buyItem, Lang.get("cosmetic.buy"));
		ItemStack tryItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 2);
		InterfaceUtils.changeName(tryItem, Lang.get("cosmetic.try"));
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		
		Runnable onTry = () -> {};
		Runnable onBuy = () -> {};
		
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 6; y++) {
				switch (pattern[y].charAt(x)) {
					case 'I' : addItem(x, y, new ItemStack(Material.DIAMOND_SWORD)); break;
					case 'R' : addButton(x, y, returnItem, this::close); break;
					case 'G' : addButton(x, y, buyItem, onBuy); break;
					case 'V' : addButton(x, y, tryItem, onTry); break;
				}
			}
		}
	}
}
