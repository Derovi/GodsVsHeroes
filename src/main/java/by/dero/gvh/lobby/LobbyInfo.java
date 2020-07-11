package by.dero.gvh.lobby;

import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class LobbyInfo {
    private int version;
    private int height;
    private int width;
    private DirectedPosition spawnPosition;
    private Position portalPosition;

    @SerializedName("monuments")
    private Map<String, DirectedPosition> classNameToMonumentPosition;


    public DirectedPosition getSpawnPosition() {
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

    public Position getPortalPosition() {
        return portalPosition;
    }

    public Map<String, DirectedPosition> getClassNameToMonumentPosition() {
        return classNameToMonumentPosition;
    }
}
