package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.FlyingText;
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
import org.bukkit.Sound;
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
    private FlyingText captureIndicator;
    private final double maxHeight = 16;
    private double currentHeight = 0;
    private int captureStatus = 0; // 0 to 36
    private int owner = 0;
    private final BukkitRunnable etherAdd;
    private final int etherDelay;
    private final int etherMineValue;

    public EtherCollector(IntPosition pos) {
        setPosition(pos);
        EtherCaptureInfo info = EtherCapture.getInstance().getEtherCaptureInfo();
        etherDelay = info.getEtherMineDelay();
        etherMineValue = info.getEtherForCollector();
        etherAdd = new BukkitRunnable() {
            double progress = 0;
            @Override
            public void run () {
                progress += (double) captureStatus / 180 / etherDelay;
                if (progress >= 1.0) {
                    EtherCapture.getInstance().addEther(owner, etherMineValue);
                    progress--;
                }
                crystal.setProgress(progress);
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
            if (captureStatus == 0) {
                owner = leading;
            }
            if (owner == leading) {
                if (captureStatus == getStages().size() * 5) {
                    break;
                }
                updateBlock(teamcode);
                captureStatus++;
            } else {
                captureStatus--;
                updateBlock('f');
            }
        }
        String indText = Lang.get(("commands." + (char)('1' + owner))).substring(0, 2) + "||||||||||||||||||";
        int idx = captureStatus / 10;
        indText = indText.substring(0, idx) + "§f" + indText.substring(idx);
        captureIndicator.setText(indText);
    }

    private void updateBlock(char teamcode) {
        if (captureStatus % 5 == 4) {
            Pair<Material, Vector> block = getStages().get(captureStatus / 5);
            Location wh = block.getValue().toLocation(location.world).add(location);
            GameUtils.changeColor(wh, teamcode);
            location.getWorld().playSound(wh, Sound.BLOCK_STONE_BREAK, 16, 1);
            if (captureStatus % 30 == 29) {
                Location at = getChanging().get(captureStatus / 30).getValue().
                        toLocation(location.world).add(location);
                GameUtils.changeColor(at, teamcode);
            }
        }
    }

    public void load() {
        crystal = new MovingCrystal(location.clone().add(0, 3, 0));
        crystal.setMaxHeight(maxHeight);
        crystal.spawn();

        captureIndicator = new FlyingText(location.clone().add(0, 3, 0), "||||||||||||||||||");
        CollectorStructure.build(location);
        etherAdd.runTaskTimer(Plugin.getInstance(), 2, 1);
    }

    public void unload() {
        captureIndicator.unload();
        etherAdd.cancel();
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
