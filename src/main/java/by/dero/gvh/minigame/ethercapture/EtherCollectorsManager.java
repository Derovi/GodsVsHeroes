package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.utils.Position;
import org.bukkit.event.Listener;

import java.util.LinkedList;
import java.util.List;

public class EtherCollectorsManager implements Listener {
    private final List<EtherCollector> collectors = new LinkedList<>();
    private final EtherCapture game;

    public EtherCollectorsManager(EtherCapture game) {
        this.game = game;
    }

    public void load() {
        for (Position position : game.getEtherCaptureInfo().getEtherCollectors()) {
            EtherCollector collector = new EtherCollector(position);
            collector.load();
            collectors.add(collector);
        }
    }

    public void unload() {
        for (EtherCollector collector : collectors) {
            collector.unload();
        }
        collectors.clear();
    }

    public List<EtherCollector> getCollectors() {
        return collectors;
    }

    public EtherCapture getGame() {
        return game;
    }
}
