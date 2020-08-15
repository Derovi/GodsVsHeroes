package by.dero.gvh.stats;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class GameStats {
    @SerializedName("_id")
    @Getter @Setter
    private int id;

    @Getter
    private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter
    private final ArrayList<Integer> percentToWin = new ArrayList<>();

    @Getter @Setter
    private int gameDurationSec;

    @Getter @Setter
    private int wonTeam;
    
    @Getter @Setter
    private long startTime;
    
    @Getter @Setter
    private ArrayList<String> deserters = new ArrayList<>();
    
    @Getter @Setter
    private String map;
    
    @Getter @Setter
    private String mode;
}
