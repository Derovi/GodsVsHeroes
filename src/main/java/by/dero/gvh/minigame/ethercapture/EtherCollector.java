package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.utils.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EtherCollector {
    private final Position position;
    private final double maxHeight = 32;
    private double currentHeight = maxHeight;
    private int captureStatus = 0;
    private int owner = -1;  // -1 if neutral

    public EtherCollector(Position position) {
        this.position = position;
    }

    public void update(List<GamePlayer> offenders) {
        // TODO
    }

    public void load() {
    }

    public void unload() {
    }

    public Position getPosition() {
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
