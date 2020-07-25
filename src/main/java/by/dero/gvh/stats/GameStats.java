package by.dero.gvh.stats;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.HashMap;

public class GameStats {
    @SerializedName("_id")
    @Getter @Setter
    private int id;

    @Getter
    private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter @Setter
    private int gameDurationSec;

    @Getter @Setter
    private int wonTeam;
    
    @Getter
    private String date;
    
    public GameStats() {
        GameStatsUtils.gameStats = this;
        GameStatsUtils.startTime = System.currentTimeMillis() / 1000;
        setDate();
        for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
            players.put(gp.getPlayer().getName(),
                    new GamePlayerStats(gp.getPlayer().getName(), gp.getClassName(), gp.getTeam()));
        }
    }
    
    public void setDate() {
        Calendar cal = Calendar.getInstance();
        String[] months = {
                "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля",
                "Августа", "Сентября", "Октября", "Ноября", "Декабря"
        };
        date = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " " +
                cal.get(Calendar.DAY_OF_MONTH) + " " + months[cal.get(Calendar.MONTH) - 1] + " " + cal.get(Calendar.YEAR);
    }
}
