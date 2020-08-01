package by.dero.gvh.lobby;

import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class LobbyInfo {
    @Getter private DirectedPosition spawnPosition;
    @Getter private Position portalPosition;

    @SerializedName("monuments") @Getter
    private Map<String, DirectedPosition> classNameToMonumentPosition;
    
    @Getter @Setter
    private DirectedPosition singleBooster;
    
    @Getter @Setter
    private DirectedPosition teamBooster;
    
    @Getter @Setter
    private DirectedPosition donateChest;
    
    @SerializedName("banners") @Getter
    private Map<String, DirectedPosition> cosmeticToBanner;
}
