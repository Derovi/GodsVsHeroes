package by.dero.gvh.lobby.interfaces;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmationInterface extends Interface {
	public ConfirmationInterface(InterfaceManager manager, Player player, String name,
	                             Runnable onAbort, Runnable onAccept, String abortName, String acceptName,
	                             List<String> loreAbort, List<String> loreAccept) {
		super(manager, player, 5, name);
		
		String[] pattern = {
				"EEEEEEEEE",
				"ERRREGGGE",
				"ERRREGGGE",
				"ERRREGGGE",
				"EEEEEEEEE",
		};
		
		
		ItemStack confirmItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
		ItemMeta meta = confirmItem.getItemMeta();
		meta.setDisplayName(acceptName);
		if (loreAccept != null) {
			meta.setLore(loreAccept);
		}
		confirmItem.setItemMeta(meta);
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		meta = returnItem.getItemMeta();
		meta.setDisplayName(abortName);
		if (loreAbort != null) {
			meta.setLore(loreAbort);
		}
		returnItem.setItemMeta(meta);
		
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
