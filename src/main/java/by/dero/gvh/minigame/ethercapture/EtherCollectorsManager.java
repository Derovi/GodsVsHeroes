package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.IntPosition;
import by.dero.gvh.utils.SafeRunnable;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EtherCollectorsManager {
    private final List<EtherCollector> collectors = new ArrayList<>();
    private final EtherCapture game;
    private SafeRunnable playersUpdater;
    private BukkitRunnable collectorsUpdater;
    private List<List<GamePlayer>> playersOnCollector;
    private boolean loaded = false;


    public EtherCollectorsManager(EtherCapture game) {
        this.game = game;
    }

    public void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        int collectorIndex = 0;
        for (IntPosition position : game.getEtherCaptureInfo().getEtherCollectors()) {
            EtherCollector collector = new EtherCollector(position, collectorIndex);
            collector.load();
            collectors.add(collector);
            collectorIndex++;
        }
        playersOnCollector = new ArrayList<>(collectors.size());
        for (int index = 0; index < collectors.size(); ++index) {
            playersOnCollector.add(new ArrayList<>());
        }
        playersUpdater = new SafeRunnable() {
            @Override
            public void run() {
                for (int index = 0; index < collectors.size(); ++index) {
                    playersOnCollector.get(index).clear();
                }
                for (GamePlayer player : game.getPlayers().values()) {
                    for (int collectorIndex = 0; collectorIndex < collectors.size(); ++collectorIndex) {
                        if (player.getPlayer().getGameMode().equals(GameMode.SURVIVAL) &&
                                collectors.get(collectorIndex).isInside(player.getPlayer().getLocation())) {
                            playersOnCollector.get(collectorIndex).add(player);
                        }
                    }
                }
            }
        };
        playersUpdater.runTaskTimer(Plugin.getInstance(), 5, 5);
        collectorsUpdater = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                for (int index = 0; index < collectors.size(); ++index) {
                    collectors.get(index).update(playersOnCollector.get(index));
                }
                ticks++;
                if (ticks % 10 == 0) {
                    game.updateDisplays();
                }
                ticks %= 10;
            }
        };
        collectorsUpdater.runTaskTimer(Plugin.getInstance(), 5, 1);
    }

    public void unload() {
        if (!loaded) {
            return;
        }
        loaded = false;
        for (EtherCollector collector : collectors) {
            collector.unload();
        }
        collectors.clear();
        if (playersUpdater.task != null) {
            playersUpdater.cancel();
        }

        collectorsUpdater.cancel();
        playersOnCollector = null;
        CollectorStructure.unload();
    }

    public List<EtherCollector> getCollectors() {
        return collectors;
    }

    public EtherCapture getGame() {
        return game;
    }
}
