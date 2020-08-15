package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.cosmetic.CosmeticSelectorInterface;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import by.dero.gvh.utils.Pair;
import by.dero.gvh.utils.SafeRunnable;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;

public class DonateSelectorInterface extends Interface {
	private SafeRunnable runnable;
	
	public DonateSelectorInterface(InterfaceManager manager, Player player) {
		super(manager, player, 3, Lang.get("interfaces.selectDonateCat"));
	}
	
	@Override
	public void open() {
		super.open();
		
		ItemStack cosmetic = new ItemStack(Material.EMERALD);
		InterfaceUtils.changeName(cosmetic, Lang.get("cosmetic.title"));
		ItemStack packs = new ItemStack(Material.ENDER_CHEST);
		InterfaceUtils.changeName(packs, Lang.get("interfaces.packsTitle"));
		ItemStack teamBooster = Heads.getHead("teamBooster");
		InterfaceUtils.changeName(teamBooster, Lang.get("lobby.teamBooster"));
		ItemStack singleBooster = Heads.getHead("singleBooster");
		InterfaceUtils.changeName(singleBooster, Lang.get("lobby.singleBooster"));
		
		addButton(3, 1, cosmetic, () -> {
			CosmeticSelectorInterface inter = new CosmeticSelectorInterface(getManager(), getPlayer());
			inter.open();
			inter.setOnBackButton(this::open);
		});
		addButton(4, 0, teamBooster, () -> {
			TeamBoostInterface inter = new TeamBoostInterface(getManager(), getPlayer(), Lobby.getInstance().getMonumentManager().getBoosters().get(0));
			inter.open();
			inter.setOnBack(this::open);
		});
		addButton(4, 2, singleBooster, () -> {
			SingleBoostInterface inter = new SingleBoostInterface(getManager(), getPlayer(), Lobby.getInstance().getMonumentManager().getBoosters().get(1));
			inter.open();
			inter.setOnBack(this::open);
		});
		
		ItemStack animItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
		ItemMeta meta = animItem.getItemMeta();
		meta.setDisplayName(" ");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		animItem.setItemMeta(meta);
		runnable = new SafeRunnable() {
			int step = 0;
			int tick = 0;
			final LinkedList<Pair<Integer, Integer>> panes = new LinkedList<>();
			@Override
			public void run() {
				if (tick == 0) {
					if (step == 0) {
						addItem(4, 1, animItem);
						panes.add(Pair.of(4, 1));
					} else if (1 <= step && step <= 4) {
						addItem(4 - step, 0, animItem);
						addItem(4 + step, 0, animItem);
						addItem(4 - step, 2, animItem);
						addItem(4 + step, 2, animItem);
						panes.add(Pair.of(4 - step, 0));
						panes.add(Pair.of(4 + step, 0));
						panes.add(Pair.of(4 - step, 2));
						panes.add(Pair.of(4 + step, 2));
					} else if (step == 5) {
						removeButton(panes.getFirst().getKey(), panes.getFirst().getValue());
						panes.removeFirst();
					} else {
						for (int i = 0; i < 4; i++) {
							removeButton(panes.getFirst().getKey(), panes.getFirst().getValue());
							panes.removeFirst();
						}
					}
					step = (step + 1) % 10;
				}
				tick = (tick + 1) % 5;
				update();
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
	
	@Override
	public void onInventoryClosed() {
		runnable.cancel();
		super.onInventoryClosed();
		getInventory().clear();
	}
}
