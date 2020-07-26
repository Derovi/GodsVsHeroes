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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class SingleBoostInterface extends Interface {
	@Setter private Runnable onBack = null;
	
	private final BukkitRunnable drawSnake;
	private final byte[] itemsMat = {1, 2, 3, 4};
	//	private final byte[] items = {8, 3, 9};
	private final List<Pair<Integer, Integer>> poses = Arrays.asList(
			Pair.of(3, 1), Pair.of(3, 2), Pair.of(3, 3), Pair.of(3, 4), Pair.of(3, 5), Pair.of(3, 6),
			Pair.of(3, 7), Pair.of(2, 7), Pair.of(1, 7), Pair.of(1, 6), Pair.of(1, 5), Pair.of(1, 4),
			Pair.of(1, 3), Pair.of(1, 2), Pair.of(1, 1), Pair.of(2, 1)
	);
	
	public SingleBoostInterface(InterfaceManager manager, Player player) {
		super(manager, player, 6, Lang.get("lobby.singleBooster"));
		
		String[] pattern = {
				"EEEEEEEEE",
				"EEEEEEEEE",
				"EEHHHHHEE",
				"EEEEEEEEE",
				"REEEEEEES",
				"RRCCCCCSS",
		};
		ItemStack[] items = new ItemStack[itemsMat.length];
		ItemStack[] itemsEnchanted = new ItemStack[itemsMat.length];
		for (int i = 0; i < itemsMat.length; i++) {
			items[i] = new ItemStack(Material.STAINED_GLASS_PANE, 1, itemsMat[i]);
			itemsEnchanted[i] = new ItemStack(Material.STAINED_GLASS_PANE, 1, itemsMat[i]);
			ItemMeta meta = itemsEnchanted[i].getItemMeta();
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.setDisplayName(" ");
			itemsEnchanted[i].setItemMeta(meta);
		}
		
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		ItemStack switchItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
		InterfaceUtils.changeName(switchItem, Lang.get("lobby.teamBooster"));
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
							TeamBoostInterface inter = new TeamBoostInterface(manager, player);
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
		
		drawSnake = new BukkitRunnable() {
			boolean stage = false;
			boolean needEnch = false;
			int itemIdx = 0;
			int snakeIdx = 0;
			final int length = 7;
			ItemStack item = items[0];
			ItemStack itemEnch = itemsEnchanted[0];
			@Override
			public void run() {
				stage |= (itemIdx > 0 && snakeIdx >= length);
				needEnch |= itemIdx > 0;
				int x = poses.get(snakeIdx).getValue(), y = poses.get(snakeIdx).getKey();
				if (stage) {
					int lst = (snakeIdx - length + poses.size()) % poses.size();
					addItem(poses.get(lst).getValue(), poses.get(lst).getKey(), item);
				} else {
					if (needEnch) {
						addItem(x, y, itemEnch);
					} else {
						addItem(x, y, item);
					}
				}
				if (needEnch) {
					addItem(poses.get(snakeIdx).getValue(), poses.get(snakeIdx).getKey(), itemEnch);
				}
				snakeIdx++;
				if (snakeIdx == poses.size()) {
					snakeIdx = 0;
					itemIdx = (itemIdx + 1) % items.length;
					itemEnch = itemsEnchanted[itemIdx];
				}
				if (snakeIdx == length && stage) {
					item = items[itemIdx];
				}
			}
		};
		drawSnake.runTaskTimer(Plugin.getInstance(), 0, 10);
	}
	
	@Override
	public void onInventoryClosed() {
		super.onInventoryClosed();
		drawSnake.cancel();
	}
}
