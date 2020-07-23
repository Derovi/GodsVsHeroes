package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.cosmetic.AllCosmetic;
import by.dero.gvh.lobby.interfaces.cosmetic.CosmeticInterfaces;
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


		addButton(0, getHeight() - 1, common, () -> {
			CosmeticInterface interfaceObject = new AllCosmetic(Lobby.getInstance().getInterfaceManager(),
					getPlayer(), "all");
			interfaceObject.open();
		});
		int x = 2, y = getHeight() - 1;
		for (UnitClassDescription desc : classes) {
			ItemStack head = Heads.getHead(desc.getName());
			InterfaceUtils.changeName(head, "§a" + Lang.get("classes." + desc.getName()));
			if (!CosmeticInterfaces.exists(desc.getName())) {
				InterfaceUtils.changeName(head, Lang.get("interfaces.soon"));
				addItem(x, y, head);
			} else addButton(x, y, head, () -> {
				try {
					Class<? extends CosmeticInterface> interfaceClass = CosmeticInterfaces.get(desc.getName());
					CosmeticInterface interfaceObject = interfaceClass.getConstructor(InterfaceManager.class,
							Player.class, String.class).newInstance(Lobby.getInstance().getInterfaceManager(),
							getPlayer(), desc.getName());
					interfaceObject.open();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			x++;
			if (x == 9) {
				x = 2;
				y--;
			}
		}
	}
}
