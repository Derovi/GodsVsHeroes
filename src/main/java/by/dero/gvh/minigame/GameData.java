package by.dero.gvh.minigame;

import by.dero.gvh.minigame.deathmatch.DeathMatchInfo;
import by.dero.gvh.minigame.ethercapture.EtherCaptureInfo;
import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;

public class GameData {
    private final StorageInterface storage;
    private GameInfo gameInfo;
    private DeathMatchInfo deathMatchInfo;
    private EtherCaptureInfo etherCaptureInfo;

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
        } catch (Exception exception) {
            System.err.println("Can't load game data!");
            exception.printStackTrace();
        }
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
    }

    public EtherCaptureInfo getEtherCaptureInfo() {
        return etherCaptureInfo;
    }
}
