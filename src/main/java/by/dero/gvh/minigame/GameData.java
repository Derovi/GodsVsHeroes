package by.dero.gvh.minigame;

import by.dero.gvh.minigame.deathmatch.DeathMatchInfo;
import by.dero.gvh.minigame.ethercapture.EtherCaptureInfo;
import by.dero.gvh.minigame.flagCapture.FlagCaptureInfo;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import lombok.Getter;

public class GameData {
    private final StorageInterface storage;
    @Getter private GameInfo gameInfo;
    @Getter private DeathMatchInfo deathMatchInfo;
    @Getter private EtherCaptureInfo etherCaptureInfo;
    @Getter private FlagCaptureInfo flagCaptureInfo;

    public GameData(StorageInterface storage) {
        this.storage = storage;
    }

    public void load() {
        try {
            gameInfo = new Gson().fromJson(DataUtils.loadOrDefault(storage, "game", "game",
                    ResourceUtils.readResourceFile("/game/game.json")), GameInfo.class);
            deathMatchInfo = new Gson().fromJson(DataUtils.loadOrDefault(storage, "game", "deathMatch",
                    ResourceUtils.readResourceFile("/game/deathMatch.json")), DeathMatchInfo.class);
            etherCaptureInfo = new Gson().fromJson(DataUtils.loadOrDefault(storage, "game", "etherCapture",
                    ResourceUtils.readResourceFile("/game/etherCapture.json")), EtherCaptureInfo.class);
            flagCaptureInfo = new Gson().fromJson(DataUtils.loadOrDefault(storage, "game", "flagCapture",
                    ResourceUtils.readResourceFile("/game/flagCapture.json")), FlagCaptureInfo.class);
        } catch (Exception exception) {
            System.err.println("Can't load game data!");
            exception.printStackTrace();
        }
    }
}
