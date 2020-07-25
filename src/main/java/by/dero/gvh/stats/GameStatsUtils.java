package by.dero.gvh.stats;

import by.dero.gvh.FlyingText;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;

public class GameStatsUtils {
	@Getter @Setter
	static GameStats gameStats;
	@Getter @Setter
	static long startTime;
	
	public static void addKill(GamePlayer target, GamePlayer killer) {
		if (target != null) {
			String name;
			GamePlayerStats stats;
			if (killer != null && !target.equals(killer)) {
				name = killer.getPlayer().getName();
				stats = gameStats.getPlayers().get(name);
				stats.setKills(stats.getKills() + 1);
			}
			name = target.getPlayer().getName();
			stats = gameStats.getPlayers().get(name);
			stats.setDeaths(stats.getDeaths() + 1);
		}
		if (Game.getInstance() instanceof DisplayInteractInterface) {
			((DisplayInteractInterface) Game.getInstance()).updateDisplays();
		}
	}
	
	//counts advancement
	public static void addKill(GamePlayer target, GamePlayer killer, Collection<GamePlayer> assists) {
		int killcnt = Game.getInstance().getRewardManager().get("killEnemy").getCount();
		GamePlayerStats stats;
		if (assists != null && !assists.isEmpty()) {
			double assistrew = killcnt * 0.4 / assists.size();
			for (GamePlayer player : assists) {
				String name = player.getPlayer().getName();
				stats = gameStats.getPlayers().get(name);
				stats.setAssists(stats.getAssists() + 1);
				stats.setAdvancement(stats.getAdvancement() + assistrew);
			}
			stats = gameStats.getPlayers().get(killer.getPlayer().getName());
			stats.setAdvancement(stats.getAdvancement() + killcnt - assistrew * assists.size());
		} else {
			stats = gameStats.getPlayers().get(killer.getPlayer().getName());
			stats.setAdvancement(stats.getAdvancement() + killcnt);
		}
		
		stats = gameStats.getPlayers().get(target.getPlayer().getName());
		stats.setAdvancement(stats.getAdvancement() - killcnt);
		addKill(target, killer);
	}
	
	public static void addDamage(GamePlayer target, GamePlayer killer, double damage) {
		if (target != null) {
			String name;
			GamePlayerStats stats;
			if (killer != null && !target.equals(killer)) {
				name = killer.getPlayer().getName();
				stats = gameStats.getPlayers().get(name);
				stats.setDamageDealt(stats.getDamageDealt() + damage);
			}
			name = target.getPlayer().getName();
			stats = gameStats.getPlayers().get(name);
			stats.setDamageTaken(stats.getDamageTaken() + damage);
		}
	}
	
	public static void addExp(GamePlayer gp, Integer value) {
		String name = gp.getPlayer().getName();
		GamePlayerStats stats = gameStats.getPlayers().get(name);
		stats.setExpGained(stats.getExpGained() + value);
		
		if (Game.getInstance().getState().equals(Game.State.GAME) &&
				Game.getInstance() instanceof DisplayInteractInterface) {
			((DisplayInteractInterface) Game.getInstance()).updateDisplays();
		}
	}
	
	
	public static void addCapturePoints(String name, Integer points) {
		GamePlayerStats stats = gameStats.getPlayers().get(name);
		stats.setCapturePoints(stats.getCapturePoints() + points);
	}

    private final LinkedList<FlyingText> texts = new LinkedList<>();
    public void spawnStats(final Location loc) {
        for (final String message : new String[]{
                Lang.get("stats.label"),
                Lang.get("stats.bestKills").replace("%pl%", getBestKills()),
                Lang.get("stats.bestDamageDealt").replace("%pl%", getBestDamageDealt()),
                Lang.get("stats.bestDeaths").replace("%pl%", getBestDeaths()),
                Lang.get("stats.bestDamageTaken").replace("%pl%", getBestDamageTaken()),
        }) {
            final FlyingText text = new FlyingText(loc.clone(), message);
            loc.subtract(0, 0.3, 0);
            texts.add(text);
        }
    }

    public void unload() {
        gameStats.getPlayers().clear();
        for (final FlyingText text : texts) {
            text.unload();
        }
    }
    
	public static String getDateString(long time) {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
		Calendar cal = Calendar.getInstance(timeZone);
		String[] months = {
				"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля",
				"Августа", "Сентября", "Октября", "Ноября", "Декабря"
		};
		String str = cal.get(Calendar.DAY_OF_MONTH) + " " + months[cal.get(Calendar.MONTH) - 1];
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (year != Calendar.getInstance(timeZone).get(Calendar.YEAR)) {
			str += " " + year;
		}
		str += ", " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
		return str;
	}
	
	private static String getBestDamageDealt() {
		String best = "";
		double val = -1;
		for (GamePlayerStats entry : gameStats.getPlayers().values()) {
			final String a = entry.getName();
			final double b = entry.getDamageDealt();
			if (val < b) {
				best = a;
				val = b;
			}
		}
		return best;
	}
	
	private static String getBestDamageTaken() {
		String best = "";
		double val = Double.MAX_VALUE;
		for (GamePlayerStats entry : gameStats.getPlayers().values()) {
			final String a = entry.getName();
			final double b = entry.getDamageTaken();
			if (val > b) {
				best = a;
				val = b;
			}
		}
		return best;
	}
	
	private static String getBestKills() {
		String best = "";
		int val = -1;
		for (GamePlayerStats entry : gameStats.getPlayers().values()) {
			final String a = entry.getName();
			final int b = entry.getKills();
			if (val < b) {
				best = a;
				val = b;
			}
		}
		return best;
	}
	
	private static String getBestDeaths() {
		String best = "";
		int val = Integer.MAX_VALUE;
		for (GamePlayerStats entry : gameStats.getPlayers().values()) {
			final String a = entry.getName();
			final int b = entry.getDeaths();
			if (val > b) {
				best = a;
				val = b;
			}
		}
		return best;
	}

	private static List<TopEntry> getKillTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingInt(a -> -game.getPlayers().get(a.getName()).getKills()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatKills").
					replace("%val%", String.valueOf(game.getPlayers().get(entry.getName()).getKills())));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
    	return list;
	}

	private static List<TopEntry> getKDATop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getTeamHeal()));
		double was = 100000;
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			GamePlayerStats cur = game.getPlayers().get(entry.getName());
			
			entry.setValue(Lang.get("stats.formatKDA").
					replace("%kills%", String.valueOf(cur.getKills())).
					replace("%deaths%", String.valueOf(cur.getDeaths())).
					replace("%assissts%", String.valueOf(cur.getAssists())).
					replace("%kda%", String.format("%.2f", cur.getKDA())));
			if (i == 0 || was > cur.getKDA()) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
			was = cur.getKDA();
		}
		
		return list;
	}

	private static List<TopEntry> getDamageTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getDamageDealt()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatDamage").
					replace("%val%", String.valueOf(Math.round(game.getPlayers().get(entry.getName()).getDamageDealt()))));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
		return list;
	}

	private static List<TopEntry> getCaptureTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingInt(a -> -game.getPlayers().get(a.getName()).getCapturePoints()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatCapture").
					replace("%val%", String.valueOf(game.getPlayers().get(entry.getName()).getCapturePoints())));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		return list;
	}

	private static List<TopEntry> getHealTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getTeamHeal()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatHeal").
					replace("%val%", String.valueOf(Math.round(game.getPlayers().get(entry.getName()).getTeamHeal()))));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
		return list;
	}

	private static List<TopEntry> getAdvancementTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getAdvancement()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatAdvancement").
					replace("%val%", String.valueOf(Math.round(game.getPlayers().get(entry.getName()).getAdvancement()))));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
		return list;
	}
	
	public static String getDurationString() {
    	String str = "";
    	long sec = System.currentTimeMillis() / 1000 - startTime;
    	if (sec >= 60) {
    		str = sec / 60 + " минут ";
	    }
    	str += sec % 60 + "секунд";
    	return str;
	}
}
