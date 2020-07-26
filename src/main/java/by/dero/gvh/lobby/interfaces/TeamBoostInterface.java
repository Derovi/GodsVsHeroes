package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import by.dero.gvh.utils.Pair;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class TeamBoostInterface extends Interface {
	@Setter private Runnable onBack = null;
	
	private BukkitRunnable drawSnake;
	private final byte[] items = {8, 3, 9};
	private ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, items[0]);
	private double itemIdx = 0;
	private int snakeIdx = 0;
	private final int length = 7;
	private final List<Pair<Integer, Integer>> poses = Arrays.asList(
			Pair.of(3, 2), Pair.of(3, 3), Pair.of(3, 4), Pair.of(3, 5), Pair.of(3, 6), Pair.of(3, 7),
			Pair.of(2, 7), Pair.of(1, 7), Pair.of(1, 6), Pair.of(1, 5), Pair.of(1, 4), Pair.of(1, 3),
			Pair.of(1, 2), Pair.of(1, 1), Pair.of(2, 1), Pair.of(3, 1)
	);
	
	private void placeSnake() {
		addItem(poses.get(snakeIdx).getValue(), poses.get(snakeIdx).getKey(), item);
		
		snakeIdx = (snakeIdx + 1) % poses.size();
		if (snakeIdx == 0) {
			itemIdx = (itemIdx + 0.5) % items.length;
			if (itemIdx == (int) itemIdx) {
				item = new ItemStack(Material.STAINED_GLASS_PANE, 1, items[(int) itemIdx]);
				InterfaceUtils.changeName(item, " ");
			} else {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
		}
	}
	
	private void removeLast() {
		int idx = (snakeIdx - length - 1 + poses.size() * 2) % poses.size();
		removeButton(poses.get(idx).getValue(), poses.get(idx).getKey());
	}
	
	public TeamBoostInterface(InterfaceManager manager, Player player) {
		super(manager, player, 6, Lang.get("lobby.teamBooster"));
		
		String[] pattern = {
				"EEEEEEEEE",
				"EEEEEEEEE",
				"EEHHHHHEE",
				"EEEEEEEEE",
				"REEEEEEES",
				"RRCCCCCSS",
		};
		InterfaceUtils.changeName(item, " ");
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		ItemStack switchItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
		InterfaceUtils.changeName(switchItem, Lang.get("lobby.singleBooster"));
		ItemStack activeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemStack[] heads = new ItemStack[5];
		Runnable[] onSelect = new Runnable[5];
		for (int i = 0; i < 5; i++) {
			heads[i] = Heads.getHead(String.valueOf((char)('1' + i)));
			onSelect[i] = () -> {};
			//TODO name of buf
			//TODO onSelect
		}
		
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				switch (pattern[y].charAt(x)) {
					case 'R' :
						addButton(x, y, returnItem, () -> {
							close();
							if (onBack != null) {
								onBack.run();
							}
						});
						break;
					case 'S' :
						addButton(x, y, switchItem, () -> {
							SingleBoostInterface inter = new SingleBoostInterface(manager, player);
							inter.setOnBack(this::open);
							close();
							inter.open();
						});
						break;
					case 'H' : addButton(x, y, heads[x-2], onSelect[x-2]); break;
					case 'C' : addItem(x, y, activeItem); break;
				}
			}
		}
		
		for (int i = 0; i < 7; i++) {
			placeSnake();
		}
		
		drawSnake = new BukkitRunnable() {
			@Override
			public void run() {
				placeSnake();
				removeLast();
			}
		};
		drawSnake.runTaskTimer(Plugin.getInstance(), 5, 5);
	}
	
	@Override
	public void onInventoryClosed() {
		super.onInventoryClosed();
		drawSnake.cancel();
	}
}
