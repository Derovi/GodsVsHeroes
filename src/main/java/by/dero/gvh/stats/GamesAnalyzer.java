package by.dero.gvh.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GamesAnalyzer {
    @Getter
    @Setter
    private List<GameStats> games = new ArrayList<>();

    public GamesAnalyzer() {
    }

    public GamesAnalyzer(List<GameStats> games) {
        this.games = games;
    }

    public int getAverageDurationSec() {
        // находишь среднюю продолжительность игр в секундах
        return 0;
    }

    public int getAverageKills() {
        // находишь среднее кол-во килов среди игроков, которые досидели до конца игры по всем играм
        return 0;
    }

    public int getAverageAssists() {
        // находишь среднее кол-во ассистов среди игроков, которые досидели до конца игры по всем играм
        return 0;
    }

    public int getAverageCapture() {
        // находишь среднее кол-во очков захвата среди игроков, которые досидели до конца игры по всем играм
        return 0;
    }

    public int getAverageTeamHeal() {
        // находишь среднее кол-во хила тиме среди игроков, которые досидели до конца игры по всем играм
        return 0;
    }

    public int getAverageExpGained() {
        // находишь среднее кол-во опыта, который получил игрок за катку (среди всех, не обязательно до конца сидевших)
        return 0;
    }

    public double getAveragePlayPercent() {
        // для каждого игрока в каждой игре находишь playTime / gameDuration и возвращаешь среднее по таким значениям
        return 0;
    }

    public List<StatsInfo> getHeroTopWinRate() {
        // Возвращаешь топ героев по винрейту
        return null;
    }

    public List<StatsInfo> getHeroTopPickRate() {
        // Возвращаешь топ героев по пикрейту (как считать норм я не знаю, я придумал такое -
        // вот допустим считаешь для warrior. Для каждой игры находишь сколько там войнов,
        // делишь на общее кол-во игроков в этой игре. Это пикрейт в этой игре. Общий пикрейт - среднее по всем играм)
        return null;
    }
}
