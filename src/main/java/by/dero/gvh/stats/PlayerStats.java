package by.dero.gvh.stats;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PlayerStats {
    @Getter
    @SerializedName("_id")
    private String name;

    public PlayerStats(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    private int wins = 0;

    @Getter
    @Setter
    private int looses = 0;

    @Getter
    private List<Integer> games = new ArrayList<>();

    public void addGame(GameStats gameStats) {
        GamePlayerStats playerStats = gameStats.getPlayers().get(name);
        if (playerStats.getTeam() == gameStats.getWonTeam()) {
            wins++;
        } else {
            looses++;
        }
        games.add(gameStats.getId());
    }
}
