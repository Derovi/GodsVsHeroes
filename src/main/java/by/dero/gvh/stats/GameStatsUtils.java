package by.dero.gvh.stats;

import by.dero.gvh.model.Lang;

import java.util.*;

public class GameStatsUtils {
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

	public static List<TopEntry> getKillTop(GameStats game) {
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

	public static List<TopEntry> getKDATop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getKDA()));
		double was = 100000;
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			GamePlayerStats cur = game.getPlayers().get(entry.getName());
			
			entry.setValue(Lang.get("stats.formatKDA").
					replace("%kills%", String.valueOf(cur.getKills())).
					replace("%deaths%", String.valueOf(cur.getDeaths())).
					replace("%assists%", String.valueOf(cur.getAssists())).
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

	public static List<TopEntry> getDamageTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getDamageDealt()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatDamage").
					replace("%val%", String.valueOf(Math.round(game.getPlayers().get(entry.getName()).getDamageDealt()) / 2)));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
		return list;
	}

	public static List<TopEntry> getCaptureTop(GameStats game) {
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

	public static List<TopEntry> getHealTop(GameStats game) {
		ArrayList<TopEntry> list = new ArrayList<>(game.getPlayers().size());
		for (GamePlayerStats playerStats : game.getPlayers().values()) {
			list.add(new TopEntry(playerStats.getName(), "", 0));
		}
		
		list.sort(Comparator.comparingDouble(a -> -game.getPlayers().get(a.getName()).getTeamHeal()));
		for (int i = 0; i < list.size(); i++) {
			TopEntry entry = list.get(i);
			entry.setValue(Lang.get("stats.formatHeal").
					replace("%val%", String.valueOf(Math.round(game.getPlayers().get(entry.getName()).getTeamHeal()) / 2)));
			if (i == 0 || !list.get(i-1).getValue().equals(entry.getValue())) {
				entry.setOrder(i + 1);
			} else {
				entry.setOrder(list.get(i-1).getOrder());
			}
		}
		
		return list;
	}

	public static List<TopEntry> getAdvancementTop(GameStats game) {
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
	
	public static String getDurationString(GameStats stats) {
    	String str = "";
    	if (stats.getGameDurationSec() >= 60) {
    		str = stats.getGameDurationSec() / 60 + " м. ";
	    }
    	str += stats.getGameDurationSec() % 60 + "с.";
    	return str;
	}
}
