package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.utils.IntPosition;
import by.dero.gvh.utils.Position;
import org.bukkit.Location;

import java.util.List;

public class EtherCollector {
    private final IntPosition position;
    private final double maxHeight = 32;
    private double currentHeight = maxHeight;
    private int captureStatus = 0;
    private int owner = -1;  // -1 if neutral
    private final CollectorStructure collectorStructure;

    public EtherCollector(IntPosition position) {
        this.position = position;
        collectorStructure = new CollectorStructure(this);
    }

    public void update(List<GamePlayer> offenders) {
        // TODO
    }

    public void load() {
        collectorStructure.buildStructure();
    }

    public void unload() {
    }

    public boolean isInside(Location location) {
        return false; //TODO
    }

    public IntPosition getPosition() {
        return position;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getCaptureStatus() {
        return captureStatus;
    }

    public void setCaptureStatus(int captureStatus) {
        this.captureStatus = captureStatus;
    }

    public double getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(double currentHeight) {
        this.currentHeight = currentHeight;
    }
}
