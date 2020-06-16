package by.dero.gvh.lobby;

import by.dero.gvh.minigame.Position;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class LobbyInfo {
    private int version;
    private int height;
    private int width;

    @SerializedName("monuments")
    private Map<String, Position> classNameToMonumentPosition;

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
