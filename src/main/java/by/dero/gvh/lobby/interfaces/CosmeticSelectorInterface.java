package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.cosmetic.AllCosmetic;
import by.dero.gvh.lobby.interfaces.cosmetic.CosmeticInterfaces;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class CosmeticSelectorInterface extends Interface {
	public CosmeticSelectorInterface(InterfaceManager manager, Player player) {
		super(manager, player, 2, Lang.get("cosmetic.title"));
	}
	
	@Override
	public void open() {
		super.open();
		
		Collection<UnitClassDescription> classes = Plugin.getInstance().getData().getClassNameToDescription().values();
		
		PlayerInfo info = Plugin.getInstance().getPlayerData().getStoredPlayerInfo(getPlayer().getName());
//		ItemStack emptySlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
//		InterfaceUtils.changeName(emptySlot, Lang.get("interfaces.empty"));
		ItemStack common = GameUtils.getHead(getPlayer());
		InterfaceUtils.changeName(common, "§9" + Lang.get("classes.all"));
		
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		InterfaceUtils.changeLore(common, Collections.singletonList("§aНажмите, чтобы открыть"));
		
		addButton(0, getHeight() - 1, common, () -> {
			CosmeticInterface interfaceObject = new AllCosmetic(Lobby.getInstance().getInterfaceManager(),
					getPlayer(), "all");
			interfaceObject.setOnBackButton(() -> {
				interfaceObject.close();
				open();
			});
			interfaceObject.open();
		});
		addButton(0, getHeight() - 2, returnItem, () -> {close(); onBackButton.run();});
		int[] idxes = {2, 2};
		for (UnitClassDescription desc : classes) {
			ItemStack head = Heads.getHead(desc.getName());
			InterfaceUtils.changeName(head, "§9" + Lang.get("classes." + desc.getName()));
			InterfaceUtils.changeLore(head, Collections.singletonList("§aОткрыть"));
			int y = getHeight() - (info.isClassUnlocked(desc.getName()) ? 1 : 2);
			int x = idxes[y];
			idxes[y]++;
			if (!CosmeticInterfaces.exists(desc.getName())) {
				InterfaceUtils.changeName(head, "§9" + Lang.get("classes." + desc.getName()));
				InterfaceUtils.changeLore(head, Collections.singletonList("§cСкоро"));
				addItem(x, y, head);
			} else addButton(x, y, head, () -> {
				try {
					Class<? extends CosmeticInterface> interfaceClass = CosmeticInterfaces.get(desc.getName());
					CosmeticInterface interfaceObject = interfaceClass.getConstructor(InterfaceManager.class,
							Player.class, String.class).newInstance(Lobby.getInstance().getInterfaceManager(),
							getPlayer(), desc.getName());
					interfaceObject.setOnBackButton(() -> {
						interfaceObject.close();
						open();
					});
					interfaceObject.open();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}
}
