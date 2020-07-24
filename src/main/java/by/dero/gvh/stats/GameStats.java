package by.dero.gvh.stats;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class GameStats {
    @Getter
    @SerializedName("_id")
    int id;

    @Getter
    private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter
    @Setter
    int gameDurationSec;

    @Getter
    @Setter
    int wonTeam;
}
