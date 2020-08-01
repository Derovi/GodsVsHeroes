package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class SlotCustomizerInterface extends Interface {
	private String className;
	private final HashMap<String, String> displayToName = new HashMap<>();
	private PlayerInfo info;
	private HashMap<String, Integer> order;
	private final Runnable saveOrder = () -> {
		if (order == null) {
			order = new HashMap<>();
			info.getItemsOrder().put(className, order);
		}
		for (int x = 0; x < 9; x++) {
			int pos = getPos(x, 0);
			
			ItemStack item = getInventory().getItem(pos);
			if (item != null && !item.getType().equals(Material.AIR)) {
				order.put(displayToName.get(item.getItemMeta().getDisplayName()), x);
			}
		}
		Plugin.getInstance().getPlayerData().savePlayerInfo(info);
	};
	
	public SlotCustomizerInterface(InterfaceManager manager, Player player, String className) {
		super(manager, player, 4, Lang.get("interfaces.slotCustomizerTitle"));
		this.className = className;
	}
	
	@Override
	public void open() {
		super.open();
		String[] pattern = {
				"IIIIIIIII",
				"EEEEEEEEE",
				"REEGEBEEE",
				"RREEHEEEE"
		};
		ItemStack skull = Heads.getHead(className);
		InterfaceUtils.changeName(skull, Lang.get("classes." + className));
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		ItemStack saveItem = new ItemStack(Material.CLAY_BALL);
		InterfaceUtils.changeName(saveItem, Lang.get("interfaces.saveResult"));
		ItemStack undoItem = new ItemStack(Material.CLAY_BALL);
		InterfaceUtils.changeName(undoItem, Lang.get("interfaces.undoResult"));
		
		putItemLine();
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < getHeight(); y++) {
				switch (pattern[y].charAt(x)) {
					case 'G' : addButton(x, y, saveItem, saveOrder); break;
					case 'B' : addButton(x, y, undoItem, this::putItemLine); break;
					case 'H' : addItem(x, y, skull); break;
					case 'R' : addButton(x, y, returnItem, () -> {
						close();
						if (onBackButton != null) {
							onBackButton.run();
						}
					}); break;
				}
			}
		}
	}
	
	private void putItemLine() {
		ArrayList<Integer> slots = new ArrayList<>();
		for (int x = 0; x < 9; x++) {
			removeButton(x, 0);
			slots.add(getPos(x, 0));
		}
		getManager().getUnlockedSlots().put(getPlayer().getUniqueId(), slots);
		
		info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
		order = info.getItemsOrder().getOrDefault(className, null);
		
		int counter = 0;
		for (String itemName : Plugin.getInstance().getData().getClassNameToDescription().get(className).getItemNames()) {
			ItemDescription desc = Plugin.getInstance().getData().getItems().get(itemName);
			if (desc.isInvisible() || desc.getSlot() != 0 || itemName.startsWith("default")) {
				continue;
			}
			
			ItemStack item = desc.getLevels().get(info.getItemLevel(className, itemName)).
					getItemStack(new CustomizationContext(getPlayer(), className));
			
			item.setAmount(1);
			displayToName.put(item.getItemMeta().getDisplayName(), itemName);
			
			if (order == null) {
				addItem(counter, 0, item);
				counter++;
			} else {
				addItem(order.get(itemName), 0, item);
			}
		}
	}
	
	@Override
	public void onInventoryClosed() {
		ArrayList<Integer> slots = getManager().getUnlockedSlots().get(getPlayer().getUniqueId());
		for (int x = 0; x < 9; x++) {
			int pos = getPos(x, 0);
			for (int i = 0; i < slots.size(); i++) {
				if (slots.get(i) == pos) {
					slots.remove(i);
					break;
				}
			}
		}
		getManager().getUnlockedSlots().put(getPlayer().getUniqueId(), slots);
		super.onInventoryClosed();
	}
}
