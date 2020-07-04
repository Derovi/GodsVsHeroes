package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.IntPosition;
import by.dero.gvh.utils.Position;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EtherCollectorsManager {
    private final List<EtherCollector> collectors = new ArrayList<>();
    private final EtherCapture game;
    private BukkitRunnable playersUpdater;
    private BukkitRunnable collectorsUpdater;
    private List<List<GamePlayer>> playersOnCollector;

    public EtherCollectorsManager(EtherCapture game) {
        this.game = game;
    }

    public void load() {
        for (IntPosition position : game.getEtherCaptureInfo().getEtherCollectors()) {
            EtherCollector collector = new EtherCollector(position);
            collector.load();
            collectors.add(collector);
        }
        playersOnCollector = new ArrayList<>(collectors.size());
        for (int index = 0; index < collectors.size(); ++index) {
            playersOnCollector.add(new ArrayList<>());
        }
        playersUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                for (int index = 0; index < collectors.size(); ++index) {
                    playersOnCollector.get(index).clear();
                }
                for (GamePlayer player : game.getPlayers().values()) {
                    for (int collectorIndex = 0; collectorIndex < collectors.size(); ++collectorIndex) {
                        if (collectors.get(collectorIndex).isInside(player.getPlayer().getLocation())) {
                            playersOnCollector.get(collectorIndex).add(player);
                        }
                    }
                }
            }
        };
        playersUpdater.runTaskTimer(Plugin.getInstance(), 5, 5);
        collectorsUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                for (int index = 0; index < collectors.size(); ++index) {
                    collectors.get(index).update(playersOnCollector.get(index));
                }
            }
        };
        collectorsUpdater.runTaskTimer(Plugin.getInstance(), 5, 1);
    }

    public void unload() {
        for (EtherCollector collector : collectors) {
            collector.unload();
        }
        collectors.clear();
        playersUpdater.cancel();
        collectorsUpdater.cancel();
        playersOnCollector = null;
    }

    public List<EtherCollector> getCollectors() {
        return collectors;
    }

    public EtherCapture getGame() {
        return game;
    }
}
