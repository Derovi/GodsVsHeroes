package by.dero.gvh.lobby;

import by.dero.gvh.utils.Position;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class LobbyInfo {
    private int version;
    private int height;
    private int width;
    private Position spawnPosition;

    @SerializedName("monuments")
    private Map<String, Position> classNameToMonumentPosition;


    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public int getVersion() {
        return version;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Map<String, Position> getClassNameToMonumentPosition() {
        return classNameToMonumentPosition;
    }
}
