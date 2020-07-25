package by.dero.gvh.stats;

import by.dero.gvh.utils.Pair;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
public class GamesAnalyzer {
    @Getter
    @Setter
    private List<GameStats> games = new ArrayList<>();

    @Builder
    @Getter
    public static class Bundle {
        private final int analyzedGames;
        private final int averageDurationSec;
        private final double averageKills;
        private final double averageAssists;
        private final double averageCapture;
        private final double averageTeamHeal;
        private final double averageExpGained;
        private final double averagePlayPercent;
        private final List<TopEntry> heroTopWinRate;
        private final List<TopEntry> heroTopPickRate;
    }

    public Bundle getBundle() {
        return Bundle.builder()
                .analyzedGames(games.size())
                .averageDurationSec(getAverageDurationSec())
                .averageKills(getAverageKills())
                .averageAssists(getAverageAssists())
                .averageCapture(getAverageCapture())
                .averageTeamHeal(getAverageTeamHeal())
                .averageExpGained(getAverageExpGained())
                .averagePlayPercent(getAveragePlayPercent())
                .heroTopWinRate(getHeroTopWinRate())
                .heroTopPickRate(getHeroTopPickRate())
                .build();
    }

    public GamesAnalyzer(List<GameStats> games) {
        this.games = games;
    }

    public int getAverageDurationSec() {
        int sum = 0;
        for (GameStats stats : games) {
            sum += stats.getGameDurationSec();
        }
        return sum / games.size();
    }

    public double getAverageKills() {
        double kills = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                kills += pl.getKills();
                cnt++;
            }
        }
        return kills / cnt;
    }

    public double getAverageAssists() {
        double assists = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                assists += pl.getAssists();
                cnt++;
            }
        }
        return assists / cnt;
    }

    public double getAverageCapture() {
        double capture = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                capture += pl.getCapturePoints();
                cnt++;
            }
        }
        return capture / cnt;
    }

    public double getAverageTeamHeal() {
        double heal = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                heal += pl.getTeamHeal();
                cnt++;
            }
        }
        return heal / cnt;
    }

    public double getAverageExpGained() {
        double exp = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                exp += pl.getExpGained();
                cnt++;
            }
        }
        return exp / cnt;
    }

    public double getAveragePlayPercent() {
        double percent = 0;
        int cnt = 0;
        for (GameStats stats : games) {
            for (GamePlayerStats pl : stats.getPlayers().values()) {
                percent += (double) pl.getPlayTimeSec() / stats.getGameDurationSec();
                cnt++;
            }
        }
        return percent / cnt;
    }

    public List<TopEntry> getHeroTopWinRate() {
        HashMap<String, Pair<Integer, Integer> > stat = new HashMap<>();
        for (GameStats game : games) {
            for (GamePlayerStats playerStats : game.getPlayers().values()) {
                String name = playerStats.getClassName();
                Pair<Integer, Integer> cur = stat.getOrDefault(name, Pair.of(0, 0));
                stat.put(name, Pair.of(cur.getKey() + playerStats.getTeam() == game.getWonTeam() ? 1 : 0, cur.getValue() + 1));
            }
        }
        ArrayList<TopEntry> list = new ArrayList<>(stat.size());
        for (Map.Entry<String, Pair<Integer, Integer> > entry : stat.entrySet()) {
            list.add(new TopEntry(entry.getKey(), "", 0));
        }
        list.sort(Comparator.comparingDouble(a -> {
            Pair<Integer, Integer> b = stat.get(a.getName());
            return -(double)b.getKey() / b.getValue();
        }));
        double was = 100000;
        for (int i = 0; i < list.size(); i++) {
            Pair<Integer, Integer> z = stat.get(list.get(i).getName());
            double cur = (double) z.getKey() / z.getValue();
            list.get(i).setValue(String.format("%.2f", cur));
            if (cur == was) {
                list.get(i).setOrder(list.get(i-1).getOrder());
            } else {
                list.get(i).setOrder(i + 1);
            }
            was = cur;
        }
        return list;
    }

    public List<TopEntry> getHeroTopPickRate() {
        int cnt = 0;
        HashMap<String, Integer> herocnt = new HashMap<>();
        for (GameStats stats : games) {
            for (GamePlayerStats playerStats : stats.getPlayers().values()) {
                cnt++;
                Integer hc = herocnt.getOrDefault(playerStats.getClassName(), 0) + 1;
                herocnt.put(playerStats.getClassName(), hc);
            }
        }
        ArrayList<TopEntry> list = new ArrayList<>(herocnt.size());
        for (Map.Entry<String, Integer> entry : herocnt.entrySet()) {
            list.add(new TopEntry(entry.getKey(), "", 0));
        }
        list.sort(Comparator.comparingInt(a -> -herocnt.get(a.getName())));
        double was = 123123;
        for (int i = 0; i < list.size(); i++) {
            double cur = (double) herocnt.get(list.get(i).getName()) / cnt;
            list.get(i).setValue(String.format("%.2f", cur));
            if (cur == was) {
                list.get(i).setOrder(list.get(i-1).getOrder());
            } else {
                list.get(i).setOrder(i + 1);
            }
            was = cur;
        }
        return list;
    }
}
