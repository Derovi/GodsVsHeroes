package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.CollectorStructure;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.utils.SafeRunnable;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class FlagPointManager {
	@Getter private final List<Location> points = new ArrayList<>();
	@Getter private final List<FlagItem> flagItems = new ArrayList<>();
	@Getter private final FlagCapture game;
	@Getter private static FlagPointManager instance;
	private SafeRunnable pointUpdater;
	private boolean loaded = false;
	
	public FlagPointManager(FlagCapture game) {
		this.game = game;
		instance = this;
	}
	
	public void load() {
		if (loaded) {
			return;
		}
		loaded = true;
		for (int team = 0; team < Game.getInstance().getInfo().getTeamCount(); team++) {
			Location point = game.getFlagCaptureInfo().getFlagPoints()[team].toLocation(Game.getWorld());
			CollectorStructure.build(point.clone().add(0, -1, 0));
			points.add(point.clone());
			FlagItem flag = new FlagItem(team, point);
			flagItems.add(flag);
		}
		
		pointUpdater = new SafeRunnable() {
			@Override
			public void run() {
			
			}
		};
		pointUpdater.runTaskTimer(Plugin.getInstance(), 1, 1);
	}
	
	public void unload() {
		if (!loaded) {
			return;
		}
		loaded = false;
		points.clear();
		pointUpdater.cancel();
		CollectorStructure.unload();
	}
}
