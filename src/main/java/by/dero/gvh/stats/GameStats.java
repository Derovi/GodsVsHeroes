package by.dero.gvh.stats;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameStats {
    @SerializedName("_id")
    @Getter @Setter
    private int id;

    @Getter
    private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter
    private final List<Integer> percentToWin = new ArrayList<>();

    @Getter @Setter
    private int gameDurationSec;

    @Getter @Setter
    private int wonTeam;
    
    @Getter @Setter
    private long startTime;
    
    public void load() {
        GameStatsUtils.gameStats = this;
        startTime = System.currentTimeMillis() / 1000;
        for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
            players.put(gp.getPlayer().getName(),
                    new GamePlayerStats(gp.getPlayer().getName(), gp.getClassName(), gp.getTeam()));
        }
    }
}
