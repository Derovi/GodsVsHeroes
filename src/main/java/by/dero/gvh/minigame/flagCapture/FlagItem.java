package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SafeRunnable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class FlagItem {
	private final int team;
	private Location pickedLoc;
	private Location location;
	private final SafeRunnable updateStatus;
	@Getter @Setter
	private GamePlayer carrier;
	private final HashMap<GamePlayer, Double> progress = new HashMap<>();
	
	public FlagItem(int team, Location loc) {
		this.team = team;
		location = loc;
		Location wh = location.clone().add(0, 2, 0);
		wh.getBlock().setType(Material.STANDING_BANNER);
		
		updateStatus = new SafeRunnable() {
			@Override
			public void run() {
				if (carrier != null) {
					location = carrier.getPlayer().getLocation();
					Location point = FlagPointManager.getInstance().getPoints().get(carrier.getTeam());
					if (location.distance(point) < 2) {
						flagCaptured();
						FlagCapture.getInstance().getFlagsCaptured()[carrier.getTeam()]++;
						FlagCapture.getInstance().updateDisplays();
						return;
					}
				} else {
					for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
						if (location.distance(gp.getPlayer().getLocation()) < 1) {
							if (gp.getTeam() != team) {
								pickup(gp);
							} else {
								progress.clear();
								flagCaptured();
							}
							return;
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
		updateStatus.runTaskTimer(Plugin.getInstance(), 5, 1);
	}
	
	public void flagCaptured() {
		updateProgress();
		pickedLoc = location = FlagPointManager.getInstance().getPoints().get(team).clone().add(0, 1, 0);
		unmountFlag();
	}
	
	public void drop() {
		updateProgress();
		unmountFlag();
	}
	
	public void unmountFlag() {
		for (Entity entity : carrier.getPlayer().getPassengers()) {
			entity.remove();
		}
		Location wh = location.clone().add(0, 1, 0);
		wh.getBlock().setType(Material.STANDING_BANNER);
		
		carrier = null;
	}
	
	public void updateProgress() {
		progress.put(carrier, progress.getOrDefault(carrier, 0.0) + location.distance(pickedLoc));
		pickedLoc = location;
	}
	
	public void pickup(GamePlayer gp) {
		carrier = gp;
		pickedLoc = gp.getPlayer().getLocation();
		
		Player player = gp.getPlayer();
		EntityArmorStand handle = new EntityArmorStand(((CraftWorld) player.getWorld()).world);
		ArmorStand stand = (ArmorStand) handle.getBukkitEntity();
		GameUtils.setInvisibleFlags(stand);
		stand.setHelmet(new ItemStack(Material.STANDING_BANNER, 1, (short) GameUtils.teamColor[team]));
		handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
		player.addPassenger(stand);
	}
	
	public void unload() {
		updateStatus.cancel();
		if (carrier == null) {
			location.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
		}
	}
}
