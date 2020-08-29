package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.minigame.CollectorStructure;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.utils.GameUtils;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class FlagPointManager {
	@Getter private final List<Location> points = new ArrayList<>();
	@Getter private final List<FlagItem> flagItems = new ArrayList<>();
	@Getter private final FlagCapture game;
	@Getter private static FlagPointManager instance;
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
			Location point = game.getFlagCaptureInfo().getFlagPoints()[team].toLocation(Game.getWorld()).add(0.5, 0, 0.5);
			CollectorStructure.build(point.clone().add(0, -1, 0));
			CollectorStructure.colorBasement(point.clone().add(0, -1, 0), GameUtils.teamColor[team]);
			points.add(point.clone());
			FlagItem flag = new FlagItem(team, point.clone().add(0, 1, 0));
			flagItems.add(flag);
		}
	}
	
	public void unload() {
		if (!loaded) {
			return;
		}
		loaded = false;
		for (FlagItem flag : flagItems) {
			flag.unmountFlag();
			flag.unload();
		}
		points.clear();
		CollectorStructure.unload();
	}
	
	public boolean isFlagOnBase(int idx) {
		return points.get(idx).distance(flagItems.get(idx).getLocation()) < 2 && flagItems.get(idx).getCarrier() == null;
	}
}
