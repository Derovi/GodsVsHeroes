package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Lang;
import by.dero.gvh.nmcapi.MovingCrystal;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.IntPosition;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.dero.gvh.minigame.ethercapture.CollectorStructure.getChanging;
import static by.dero.gvh.minigame.ethercapture.CollectorStructure.getStages;

public class EtherCollector {
    private Location location;
    private MovingCrystal crystal;
    private final double maxHeight = 16;
    private double currentHeight = 0;
    private int captureStatus = 0; // 0 to 36
    private int offender = -1;
    private int owner = -1;  // -1 if neutral
    private final BukkitRunnable etherAdd;
    private final int etherDelay;
    private final int etherMineValue;

    public EtherCollector(IntPosition pos) {
        setPosition(pos);
        EtherCaptureInfo info = EtherCapture.getInstance().getEtherCaptureInfo();
        etherDelay = info.getEtherMineDelay();
        etherMineValue = info.getEtherForCollector();
        etherAdd = new BukkitRunnable() {
            @Override
            public void run () {
                if (owner != -1) {
                    EtherCapture.getInstance().addEther(owner, etherMineValue);
                }
            }
        };
    }

    private final HashMap<Integer, Integer> counter = new HashMap<>();
    public void update(List<GamePlayer> offenders) {
        if (offenders == null || offenders.size() == 0) {
            return;
        }

        counter.clear();
        for (GamePlayer gp : offenders) {
            counter.put(gp.getTeam(),counter.getOrDefault(gp.getTeam(), 0) + 1);
        }
        int leading = -1, val = -1;
        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            if (val < entry.getValue()) {
                val = entry.getValue();
                leading = entry.getKey();
            }
        }
        val = val * 2 - offenders.size();
        char teamcode = Lang.get(("commands." + (char)('1' + leading))).charAt(1);
        for (int i = 0; i < val; i++) {
            if (offender == -1 || offender == leading) {
                if (captureStatus == getStages().size() * 5) {
                    break;
                }
                offender = leading;
                updateBlock(teamcode);
                captureStatus++;
                if (captureStatus == getStages().size() * 5) {
                    owner = leading;
                    if (etherAdd.task == null) {
                        etherAdd.runTaskTimer(Plugin.getInstance(), etherDelay, etherDelay);
                    }
                }
            } else {
                captureStatus--;
                updateBlock('f');
                if (captureStatus == 0) {
                    owner = -1;
                    offender = -1;
                    if (etherAdd.task != null) {
                        etherAdd.cancel();
                    }
                }
            }
        }
    }

    private void updateBlock(char teamcode) {
        if (captureStatus % 30 == 29) {
            GameUtils.changeColor(getChanging().get(captureStatus / 30).getValue().
                    toLocation(location.world).add(location), teamcode);
        }

        Pair<Material, Vector> block = getStages().get(captureStatus / 5);
        GameUtils.changeColor(block.getValue().toLocation(location.world).add(location), teamcode);
    }

    public void load() {
        crystal = new MovingCrystal(location.clone().add(0, 3, 0));
        crystal.setMaxHeight(maxHeight);
        crystal.spawn();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run () {
                crystal.setProgress((double) captureStatus / 180);
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
        EtherCapture.getInstance().getRunnables().add(runnable);

        CollectorStructure.build(location);
    }

    public void unload() {
        if (etherAdd.task != null) {
            etherAdd.cancel();
        }
    }

    public boolean isInside(Location location) {
        return location.distance(this.location) < 4;
    }

    public double getCurrentHeight () {
        return currentHeight;
    }

    public void setCurrentHeight (double currentHeight) {
        this.currentHeight = currentHeight;
    }

    public int getCaptureStatus () {
        return captureStatus;
    }

    public void setCaptureStatus (int captureStatus) {
        this.captureStatus = captureStatus;
    }

    public IntPosition getPosition() {
        return new IntPosition(location);
    }

    public void setPosition(IntPosition pos) {
        location = pos.toLocation(Minigame.getInstance().getWorld());
    }

    public int getOwner () {
        return owner;
    }

    public void setOwner (int owner) {
        this.owner = owner;
    }
}
