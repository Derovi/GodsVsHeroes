package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.RewardManager;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MessagingUtils;
import by.dero.gvh.utils.SafeRunnable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlagItem {
	private static final long droppedDelay = 3000;
	private static final long droppedDeathDelay = 5000;
	private static final double returnMult = 0.5;
	@Setter private long droppedLast = 0;
	@Setter private long droppedDeathLast = 0;
	private final int team;
	@Getter private Location pickedLoc;
	@Getter private Location location;
	private final SafeRunnable updateStatus;
	@Getter private GamePlayer carrier;
	private EntityArmorStand handle = null;
	private final HashMap<GamePlayer, Double> progress = new HashMap<>();
	
	public FlagItem(int team, Location loc) {
		this.team = team;
		location = loc.clone();
		pickedLoc = location.clone();
		placeBanner();
		updateStatus = new SafeRunnable() {
			@Override
			public void run() {
				if (carrier != null) {
					location = carrier.getPlayer().getLocation().clone().add(0.2, 0, 0.2);
					Location point = FlagPointManager.getInstance().getPoints().get(carrier.getTeam());
					if (location.distance(point) < 2) {
						if (FlagPointManager.getInstance().getPoints().get(carrier.getTeam()).distance(
								FlagPointManager.getInstance().getFlagItems().get(carrier.getTeam()).getLocation()) < 2) {
							
							FlagCapture.getInstance().getFlagsCaptured()[carrier.getTeam()]++;
							FlagCapture.getInstance().updateDisplays();
							flagCaptured();
						} else {
							MessagingUtils.sendActionBar(Lang.get("game.cantDeliverFlag"), carrier.getPlayer());
						}
					}
				} else {
					Location point = FlagPointManager.getInstance().getPoints().get(team).clone().add(0, 1, 0);
					for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
						if (!GameUtils.isDeadPlayer(gp.getPlayer()) &&
								location.distance(gp.getPlayer().getLocation()) < 2) {
							if (gp.getTeam() != team && !gp.isInventoryHided()) {
								if (System.currentTimeMillis() > droppedLast + droppedDelay &&
										System.currentTimeMillis() > droppedDeathLast + droppedDeathDelay) {
									
									String flagTaken = Lang.get("game.flagTakenInform").
											replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam()))).
											replace("%en%", GameUtils.getColorPrefix(team));
									if (location.distance(FlagPointManager.getInstance().getPoints().get(team)) <= 2) {
										location.getWorld().playSound(location, Sound.ENTITY_ENDERDRAGON_GROWL, 3, 1);
										for (GamePlayer other : Game.getInstance().getPlayers().values()) {
											if (other.getTeam() == team) {
												MessagingUtils.sendSubtitle(flagTaken, Collections.singletonList(other));
											}
										}
									}
									
									Bukkit.getServer().broadcastMessage(flagTaken);
									MessagingUtils.sendSubtitle(flagTaken, Game.getInstance().getPlayers().values());
									ItemStack[] armor = gp.getPlayer().getInventory().getArmorContents();
									gp.hideInventory();
									gp.getPlayer().getInventory().setArmorContents(armor);
									for (int i = 0; i < 9; i ++) {
										gp.getPlayer().getInventory().setItem(i, getFlag());
									}
									pickup(gp);
									FlagCapture.getInstance().updateDisplays();
									break;
								}
							} else {
								if (point.distance(gp.getPlayer().getLocation()) > 2) {
									location = gp.getPlayer().getLocation().clone().add(0.2, 0, 0.2);
									double[] dst = {FlagPointManager.getInstance().getPoints().get(0).distance(location),
													FlagPointManager.getInstance().getPoints().get(1).distance(location)};
									double rew = dst[team] / (dst[0] + dst[1]) * returnMult *
											Game.getInstance().getRewardManager().get("flagCaptured").getCount();
									String name = gp.getPlayer().getName();
									Game.getInstance().getGameStatsManager().addExp(gp, rew);
									Game.getInstance().getRewardManager().addExp(name, rew);
									MessagingUtils.sendSubtitle(Lang.get("rewmes.flagCapture").
											replace("%exp%", GameUtils.getString(rew)), gp.getPlayer(), 0, 20, 0);
									
									handle.die();
									handle = null;
									
									progress.clear();
									location = point.clone();
									pickedLoc = point.clone();
									while (location.getBlock().getType().equals(Material.AIR)) {
										location = location.add(0, -1, 0);
									}
									location.add(0, 1, 0);
									placeBanner();
									FlagCapture.getInstance().updateDisplays();
									break;
								}
							}
							break;
						}
					}
				}
				for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
					if (gp.getTeam() == team) {
						gp.getPlayer().setCompassTarget(location);
					}
				}
			}
		};
		updateStatus.runTaskTimer(Plugin.getInstance(), 5, 2);
	}
	
	public ItemStack getFlag() {
		ItemStack flg = new ItemStack(Material.BANNER);
		BannerMeta meta = (BannerMeta) flg.getItemMeta();
		meta.setDisplayName(Lang.get("commands." + (char)('1' + team)));
		meta.setBaseColor(GameUtils.dyeTeamColor[team]);
		flg.setItemMeta(meta);
		return flg;
	}
	
	public void placeBanner() {
		handle = new EntityArmorStand(((CraftWorld) location.getWorld()).world);
		handle.setPosition(location.x, location.y - 1.5, location.z);
		ArmorStand stand = (ArmorStand) handle.getBukkitEntity();
		GameUtils.setInvisibleFlags(stand);
		handle.invulnerable = true;
//		handle.setMarker(true);
		
		stand.setHelmet(getFlag());
		handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
//		Location wh = location.clone().add(0, 1, 0);
//		Block block = wh.getBlock();
//		block.setType(Material.STANDING_BANNER);
//		Banner banner = (Banner) block.getState();
//		banner.setBaseColor(GameUtils.dyeTeamColor[team]);
//		banner.update();
//		Bukkit.getServer().broadcastMessage(String.valueOf(block.getType()));
//		Bukkit.getServer().broadcastMessage(String.valueOf(banner.getBaseColor()));
	}
	
	public void flagCaptured() {
		updateProgress();
		for (Map.Entry<GamePlayer, Double> entry : progress.entrySet()) {
			if (entry.getKey() != null && GameUtils.getObject(entry.getKey().getPlayer()) != null) {
				RewardManager manager = Game.getInstance().getRewardManager();
				
				double mult = (double) manager.get("flagCaptured").getCount() /
						FlagPointManager.getInstance().getPoints().get(0).distance(FlagPointManager.getInstance().getPoints().get(1));
				double al = Game.getInstance().getMultiplier(entry.getKey());
				
				int cnt = (int) Math.ceil(entry.getValue() * mult * al);
				String name = entry.getKey().getPlayer().getName();
				Game.getInstance().getGameStatsManager().addCapturePoints(name, (int) (double) entry.getValue());
				Game.getInstance().getGameStatsManager().addExp(entry.getKey(), cnt);
				Game.getInstance().getRewardManager().addExp(name, cnt);
				MessagingUtils.sendSubtitle(Lang.get("rewmes.flagCapture").
						replace("%exp%", String.valueOf(cnt)), entry.getKey().getPlayer(), 0, 20, 0);
				
			}
		}
		Bukkit.getServer().broadcastMessage(Lang.get("game.flagDeliverInform").
				replace("%com%", Lang.get("commands." + (char)('1' + carrier.getTeam()))));
		Game.getWorld().playSound(location, Sound.ENTITY_ENDERDRAGON_GROWL, 100, 1);
		progress.clear();
		
		location = FlagPointManager.getInstance().getPoints().get(team).clone().add(0, 1, 0);
		pickedLoc = location.clone();
		unmountFlag();
	}
	
	public void drop() {
		updateProgress();
		unmountFlag();
	}
	
	public void unmountFlag() {
		if (carrier == null) {
			return;
		}
		for (Entity entity : carrier.getPlayer().getPassengers()) {
			entity.remove();
		}
		while (location.getBlock().getType().equals(Material.AIR)) {
			location = location.add(0, -1, 0);
		}
		location.add(0, 1, 0);
		placeBanner();
		
		carrier.showInventory();
		carrier = null;
		location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, 2, 1);
	}
	
	public void updateProgress() {
		progress.put(carrier, progress.getOrDefault(carrier, 0.0) + location.distance(pickedLoc));
		pickedLoc = location.clone();
	}
	
	public void pickup(GamePlayer gp) {
		carrier = gp;
		
		this.handle.die();
		this.handle = null;
		pickedLoc = gp.getPlayer().getLocation();
		
		Player player = gp.getPlayer();
		EntityArmorStand handle = new EntityArmorStand(((CraftWorld) player.getWorld()).world);
		ArmorStand stand = (ArmorStand) handle.getBukkitEntity();
		GameUtils.setInvisibleFlags(stand);
		handle.invulnerable = true;
		handle.setMarker(true);
		ItemStack flg = new ItemStack(Material.BANNER);
		BannerMeta meta = (BannerMeta) flg.getItemMeta();
		meta.setBaseColor(GameUtils.dyeTeamColor[team]);
		flg.setItemMeta(meta);
		stand.setHelmet(flg);
		handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
		player.addPassenger(stand);
		location.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
	}
	
	public void unload() {
		updateStatus.cancel();
		if (handle != null) {
			handle.die();
			handle = null;
		}
	}
}
