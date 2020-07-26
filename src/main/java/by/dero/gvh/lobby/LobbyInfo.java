package by.dero.gvh.lobby;

import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class LobbyInfo {
    private DirectedPosition spawnPosition;
    private Position portalPosition;

    @SerializedName("monuments")
    private Map<String, DirectedPosition> classNameToMonumentPosition;

    public DirectedPosition getSpawnPosition() {
        return spawnPosition;
    }

    public Position getPortalPosition() {
        return portalPosition;
    }

    public Map<String, DirectedPosition> getClassNameToMonumentPosition() {
        return classNameToMonumentPosition;
    }
    
    @Getter @Setter
    private DirectedPosition singleBooster;
    
    @Getter @Setter
    private DirectedPosition teamBooster;
}
