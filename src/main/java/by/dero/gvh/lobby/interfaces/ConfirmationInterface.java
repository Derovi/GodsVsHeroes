package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmationInterface extends Interface {
	public ConfirmationInterface(InterfaceManager manager, Player player, String name, Runnable onAbort, Runnable onAccept) {
		super(manager, player, 5, name);
		
		String[] pattern = {
				"EEEEEEEEE",
				"ERRREGGGE",
				"ERRREGGGE",
				"ERRREGGGE",
				"EEEEEEEEE",
		};
		
		
		ItemStack confirmItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
		InterfaceUtils.changeName(confirmItem, Lang.get("interfaces.confirm"));
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				switch (pattern[y].charAt(x)) {
					case 'R' : addButton(x, y, returnItem, onAbort); break;
					case 'G' : addButton(x, y, confirmItem, onAccept); break;
				}
			}
		}
	}
}
