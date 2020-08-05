package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.interfaces.Interface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.model.Cosmetic;
import by.dero.gvh.model.CosmeticInfo;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.InterfaceUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeathTextCosmetics extends Interface {
	private final List<CosmeticInterface.CosmeticButton> cosmeticButtons = new ArrayList<>();
	
	public DeathTextCosmetics(InterfaceManager manager, Player player) {
		super(manager, player, 2, Lang.get("cosmetic.deathText"));
		for (int x = 0; x < 7; x++) {
			String name = "deathText" + x;
			CosmeticInfo info = Plugin.getInstance().getCosmeticManager().getCustomizations().getOrDefault(name, null);
			if (info != null) {
				registerCosmetic(new CosmeticInterface.CosmeticButton(x + 2, 1, name));
			} else {
				break;
			}
		}
		update();
	}
	
	public void update() {
		clear();
		ItemStack backItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(backItem, Lang.get("interfaces.back"));
		addButton(0, 1, backItem, () -> {
			if (onBackButton != null) {
				onBackButton.run();
			}
		});
		addButton(1, 1, backItem, () -> {
			if (onBackButton != null) {
				onBackButton.run();
			}
		});
		addButton(0, 0, backItem, () -> {
			if (onBackButton != null) {
				onBackButton.run();
			}
		});
		addButton(1, 0, backItem, () -> {
			if (onBackButton != null) {
				onBackButton.run();
			}
		});
		
		PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
		Map<String, Cosmetic> customizations = playerInfo.getCosmetics();
		for (CosmeticInterface.CosmeticButton button : cosmeticButtons) {
			String name = button.getCosmeticName();
			CosmeticInfo info = Plugin.getInstance().getCosmeticManager().getCustomizations().get(name);
			ItemStack item = info.getItemStack();
			ItemStack subItem;
			int state = 0;
			if (customizations.containsKey(name)) {
				state = 1;
				if (customizations.get(name).isEnabled()) {
					state = 2;
				}
			}
			Runnable runnable;
			if (state == 0) {
				item = info.getItemStack(true);
				subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
				InterfaceUtils.changeName(subItem, Lang.get("cosmetic.buy"));
				runnable = () -> {
					getPlayer().playSound(getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					BuyCosmeticInterface buyCosmetic = new BuyCosmeticInterface(getManager(), getPlayer(), name);
					buyCosmetic.setOnBackButton(() -> {
						buyCosmetic.close();
						update();
						open();
					});
					buyCosmetic.open();
				};
			} else if (state == 1) {
				subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
				InterfaceUtils.changeName(subItem, Lang.get("cosmetic.enable"));
				runnable = () -> {
					getPlayer().playSound(getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					playerInfo.enableCosmetic(name);
					Plugin.getInstance().getPlayerData().savePlayerInfo(getPlayer().getName(), playerInfo);
					update();
				};
			} else {
				subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
				InterfaceUtils.changeName(subItem, Lang.get("cosmetic.disable"));
				runnable = () -> {
					getPlayer().playSound(getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					playerInfo.disableCosmetic(name);
					Plugin.getInstance().getPlayerData().savePlayerInfo(getPlayer().getName(), playerInfo);
					update();
				};
			}
			addButton(button.getX(), button.getY(), item, runnable);
			addButton(button.getX(), button.getY() - 1, subItem, runnable);
		}
	}
	
	@AllArgsConstructor
	public static class CosmeticButton {
		@Getter @Setter
		private int x;
		
		@Getter @Setter
		private int y;
		
		@Getter @Setter
		private String cosmeticName;
	}
	
	public void registerCosmetic(CosmeticInterface.CosmeticButton button) {
		cosmeticButtons.add(button);
	}
}
