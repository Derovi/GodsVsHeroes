package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.donate.Donate;
import by.dero.gvh.donate.DonateType;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.monuments.BoosterStand;
import by.dero.gvh.model.Booster;
import by.dero.gvh.model.BoosterInfo;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.InterfaceUtils;
import by.dero.gvh.utils.Pair;
import by.dero.gvh.utils.SafeRunnable;
import com.google.common.collect.Lists;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SingleBoostInterface extends Interface {
	@Setter private Runnable onBack = null;
	
	private BukkitRunnable drawSnake;
//	private final byte[] itemsMat = {1, 2, 3, 4};
	private final byte[] itemsMat = {8, 9, 3};
	private final List<Pair<Integer, Integer>> poses = Arrays.asList(
			Pair.of(3, 1), Pair.of(3, 2), Pair.of(3, 3), Pair.of(3, 4), Pair.of(3, 5), Pair.of(3, 6),
			Pair.of(3, 7), Pair.of(2, 7), Pair.of(1, 7), Pair.of(1, 6), Pair.of(1, 5), Pair.of(1, 4),
			Pair.of(1, 3), Pair.of(1, 2), Pair.of(1, 1), Pair.of(2, 1)
	);
	
	private final ArrayList<SafeRunnable> runnables = new ArrayList<>();
	
	private final BoosterStand stand;
	
	public SingleBoostInterface(InterfaceManager manager, Player player, BoosterStand stand) {
		super(manager, player, 6, Lang.get("lobby.singleBooster"));
		this.stand = stand;
	}
	
	@Override
	public void open() {
		super.open();
		String[] pattern = {
				"EEEEEEEEE",
				"EEEEEEEEE",
				"EEHHHHHEE",
				"EEEEEEEEE",
				"REEEEEEES",
				"RRCCCCVSS",
		};
		ItemStack[] items = new ItemStack[itemsMat.length];
		ItemStack[] itemsEnchanted = new ItemStack[itemsMat.length];
		PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
		for (int i = 0; i < itemsMat.length; i++) {
			items[i] = new ItemStack(Material.STAINED_GLASS_PANE, 1, itemsMat[i]);
			ItemMeta meta = items[i].getItemMeta();
			meta.setDisplayName(" ");
			items[i].setItemMeta(meta);
			itemsEnchanted[i] = new ItemStack(Material.STAINED_GLASS_PANE, 1, itemsMat[i]);
			meta = itemsEnchanted[i].getItemMeta();
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.setDisplayName(" ");
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemsEnchanted[i].setItemMeta(meta);
		}
		
		ItemStack returnItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		InterfaceUtils.changeName(returnItem, Lang.get("interfaces.back"));
		ItemStack switchItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
		InterfaceUtils.changeName(switchItem, Lang.get("lobby.teamBooster"));
		ItemStack activeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		InterfaceUtils.changeName(activeItem, Lang.get("interfaces.empty"));
		activeItem.setLore(Lists.newArrayList(Lang.get("interfaces.notActive")));
		ItemStack queueActiveItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
		ItemStack permMultItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 10);
		double mult = 1;
		for (Booster booster : info.getBoosters()) {
			if (booster.getName().equals("L5")) {
				mult += booster.getBonus();
			}
		}
		InterfaceUtils.changeName(permMultItem, Lang.get("interfaces.permMult").
				replace("%val%", String.valueOf(Math.round(mult * 100))));
		InterfaceUtils.changeLore(permMultItem,
				Collections.singletonList("§3§lПостоянный §fбонус §8» §a+§l" + Math.round(mult * 100 - 100) + "%"));
		
		Color[] fwColors = {
				Color.GREEN, Color.YELLOW, Color.BLUE, Color.PURPLE, Color.RED
		};
		ItemStack[] heads = new ItemStack[5];
		Runnable[] onSelect = new Runnable[5];
		for (int i = 0; i < 5; i++) {
			String boostName = "L" + (char)('1' + i);
			BoosterInfo boosterInfo = Plugin.getInstance().getBoosterManager().getBoosters().get(boostName);
			heads[i] = GameUtils.getBoosterHead(boostName);
			int finalI = i;
			onSelect[i] = () -> {
				ConfirmationInterface inter = new ConfirmationInterface(getManager(), getPlayer(),
						Lang.get("interfaces.confirmBuy"), this::open, () -> {
					Donate donate = Donate.builder()
							.price(boosterInfo.getCost())
							.type(DonateType.BOOSTER)
							.description("Booster " + boostName)
							.onSuccessful(() -> {
								getPlayer().playSound(getPlayer().getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
								BoosterStand stand = Lobby.getInstance().getMonumentManager().getBoosters().get(1);
								Lobby.getInstance().getChest().addAnim(2, getPlayer(), null, () ->
										Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () ->
												stand.getAnims().add(fwColors[finalI]),
										Lobby.getInstance().getChest().getAnimDuration() - 20));
								info.activateBooster(boostName);
								Plugin.getInstance().getPlayerData().savePlayerInfo(info);
								getPlayer().sendMessage(Lang.get("interfaces.thxBuyBooster"));
							})
							.onError(() -> {

							}).build();
					donate.apply(getPlayer());
					open();
				}, Lang.get("interfaces.back"), Lang.get("interfaces.confirm"), null, heads[finalI].getLore());
				inter.open();
			};
		}

		info.removeExpiredBoosters();
		long[] boosters = new long[5];
		for (int i = 0; i < 5; i++) {
			boosters[i] = -1;
		}
		for (Booster booster : info.getBoosters()) {
			if (booster.getName().charAt(0) == 'L' && booster.getName().charAt(1) != '5') {
				int idx = Integer.parseInt(String.valueOf(booster.getName().charAt(1))) - 1;
				boosters[idx] = Math.max(boosters[idx], booster.getExpirationTime());
			}
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
							TeamBoostInterface inter = new TeamBoostInterface(getManager(), getPlayer(), stand);
							inter.setOnBack(this::open);
							close();
							inter.open();
						});
						break;
					case 'H' :
						addButton(x, y, heads[x-2], onSelect[x-2]); break;
					case 'C' :
						if (boosters[x-2] > 0) {
							List<String> lore = new ArrayList<>(heads[x-2].getLore());
							InterfaceUtils.changeName(queueActiveItem, heads[x-2].getItemMeta().getDisplayName());
							addButton(x, y, queueActiveItem, onSelect[x-2]);
							
							int finalX = x;
							int finalY = y;
							SafeRunnable runnable = new SafeRunnable() {
								@Override
								public void run() {
									long left = boosters[finalX - 2] - System.currentTimeMillis() / 1000;
									if (left > 0) {
										lore.set(lore.size() - 1, Lang.get("interfaces.queueActive").
												replace("%val%", InterfaceUtils.getLeftTimeString((int) left)));
										getInventory().getItem(getPos(finalX, finalY)).setLore(lore);
									} else {
										this.cancel();
										InterfaceUtils.changeName(activeItem, heads[finalX - 2].getItemMeta().getDisplayName());
										addButton(finalX, finalY, activeItem, onSelect[finalX-2]);
									}
								}
							};
							runnable.runTaskTimer(Plugin.getInstance(), 0, 20);
							runnables.add(runnable);
						} else {
							InterfaceUtils.changeName(activeItem, heads[x - 2].getItemMeta().getDisplayName());
							addButton(x, y, activeItem, onSelect[x-2]);
						}
						break;
					case 'V' :
						addButton(x, y, permMultItem, onSelect[x-2]);
						break;
				}
			}
		}
		
		drawSnake = new BukkitRunnable() {
			boolean start = true;
			boolean stage = false;
			boolean needEnch = false;
			int itemIdx = 0;
			int snakeIdx = 0;
			final int length = 7;
			ItemStack item = items[0];
			ItemStack itemEnch = itemsEnchanted[0];
			int ticks = 0;
			@Override
			public void run() {
				if (ticks == 0) {
					for (int i = 0; i < (start ? poses.size() : 1); i++) {
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
							itemEnch = itemsEnchanted[itemIdx];
							itemIdx = (itemIdx + 1) % items.length;
						}
						if (snakeIdx == length && needEnch) {
							item = items[itemIdx];
						}
						update();
					}
					start = false;
				}
				ticks = (ticks + 1) % 3;
				update();
			}
		};
		drawSnake.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
	
	@Override
	public void onInventoryClosed() {
		super.onInventoryClosed();
		drawSnake.cancel();
		
		for (SafeRunnable runnable : runnables) {
			runnable.cancel();
		}
		runnables.clear();
		getInventory().clear();
	}
}
