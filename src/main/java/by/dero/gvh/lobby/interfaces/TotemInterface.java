package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.donate.Donate;
import by.dero.gvh.donate.DonateType;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.InterfaceUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class TotemInterface extends Interface {
	@AllArgsConstructor
	private static class Level {
		private final int cost;
		private final int bonus;
	}
	
	private static final Level[] levels = {
			new Level(0, 20), new Level(9, 25), new Level(11, 30), new Level(13, 35), new Level(15, 40),
			new Level(17, 45), new Level(19, 50), new Level(21, 55), new Level(23, 60), new Level(25, 65),
			new Level(27, 70), new Level(29, 75), new Level(31, 80), new Level(33, 85), new Level(35, 90),
			new Level(37, 95), new Level(39, 100), new Level(41, 105), new Level(43, 110), new Level(45, 115),
			new Level(47, 120), new Level(49, 125), new Level(51, 130), new Level(53, 135), new Level(55, 140),
			new Level(57, 145), new Level(59, 150), new Level(61, 155), new Level(63, 160), new Level(65, 175),
			new Level(67, 170), new Level(69, 175), new Level(71, 180), new Level(73, 185), new Level(75, 190),
			new Level(77, 195), new Level(79, 200), new Level(81, 205), new Level(83, 210), new Level(85, 215)
	};
	
	private final PlayerInfo info;
	public TotemInterface(InterfaceManager manager, Player player) {
		super(manager, player, 6, Lang.get("interfaces.dailyBonus"));
		info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
	}
	
	@Override
	public void update() {
		clear();
		String[] pattern =  {
				"RRRRRRRRR",
				"REEESEEER",
				"REUUETTER",
				"REUUETTER",
				"REEEEEEER",
				"RREEHEERR",
		};
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		ItemStack takeItem;
		int bonus = levels[info.getTotemLevel()].bonus;
		if (System.currentTimeMillis() / 1000 - info.getTotemLastTaken() >= 86400) {
			takeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
			InterfaceUtils.changeName(takeItem, "§aЗабрать §8» §6§l" + bonus + " §fопыта.");
		} else {
			takeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			InterfaceUtils.changeName(takeItem, "§cБонус еще не накапал");
			takeItem.setLore(Collections.singletonList(
					"§fДоход §8» §6§l" + levels[info.getTotemLevel()].bonus + " §fопыта."));
		}
		ItemStack upgradeItem;
		if (info.getTotemLevel() != levels.length - 1) {
			upgradeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
			InterfaceUtils.changeName(upgradeItem, "§aУлучшить§8: §f" + bonus + "§8->§b" + levels[info.getTotemLevel() + 1].bonus);
			
			upgradeItem.setLore(Lists.newArrayList(
					Lang.get("interfaces.cristCostLore").
							replace("%cost%", String.valueOf(levels[info.getTotemLevel() + 1].cost))));
		} else {
			upgradeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 2);
			InterfaceUtils.changeName(upgradeItem, "§5Максимальный уровень");
			
			upgradeItem.setLore(Collections.singletonList(
					"§fДоход §8» §6§l" + levels[info.getTotemLevel()].bonus + " §fопыта."));
		}
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				switch (pattern[y].charAt(x)) {
					case 'R':
						addButton(x, y, returnItem, this::close);
						break;
					case 'T':
						addButton(x, y, takeItem, () -> {
							if (takeItem.getDurability() == 5) {
								info.setBalance(info.getBalance() + bonus);
								info.setTotemLastTaken((int) (System.currentTimeMillis() / 1000));
								Plugin.getInstance().getPlayerData().savePlayerInfo(info);
								Lobby.getInstance().updateDisplays(getPlayer());
								update();
							}
						});
						break;
					case 'U':
						addButton(x, y, upgradeItem, () -> {
							if (info.getTotemLevel() != levels.length - 1) {
								ConfirmationInterface inter = new ConfirmationInterface(getManager(), getPlayer(),
										Lang.get("interfaces.confirmBuy"), this::open, () -> {
									Donate donate = Donate.builder()
											.price(levels[info.getTotemLevel() + 1].cost)
											.type(DonateType.TOTEM)
											.description("Totem " + (info.getTotemLevel() + 1))
											.onSuccessful(() -> {
												getPlayer().playSound(getPlayer().getEyeLocation(),
														Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
												info.setTotemLevel(info.getTotemLevel() + 1);
												Plugin.getInstance().getPlayerData().savePlayerInfo(info);
											})
											.onError(() -> {
											
											}).build();
									donate.apply(getPlayer());
									open();
								}, Lang.get("interfaces.back"), Lang.get("interfaces.confirm"), null,
										Lists.newArrayList(
												"§fДоход §8» §6§l" + levels[info.getTotemLevel() + 1].bonus + " §fопыта.",
												Lang.get("interfaces.cristCostLore").
														replace("%cost%", String.valueOf(levels[info.getTotemLevel() + 1].cost))));
								inter.setOnBackButton(this::open);
								inter.open();
							}
						});
				}
			}
		}
		super.update();
	}
	
	@Override
	public void open() {
		super.open();
		update();
	}
}
