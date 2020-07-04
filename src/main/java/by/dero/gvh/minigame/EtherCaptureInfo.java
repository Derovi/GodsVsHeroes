package by.dero.gvh.minigame;

import by.dero.gvh.utils.Position;

public class EtherCaptureInfo {
    private Position[] etherCollectors;
    private int etherToWin;
    private int etherForKill;
    private int etherForCollector;
    private int captureTime;

    public Position[] getEtherCollectors() {
        return etherCollectors;
    }

    public void setEtherCollectors(Position[] etherCollectors) {
        this.etherCollectors = etherCollectors;
    }

    public int getEtherToWin() {
        return etherToWin;
    }

    public void setEtherToWin(int etherToWin) {
        this.etherToWin = etherToWin;
    }

    public int getEtherForKill() {
        return etherForKill;
    }

    public void setEtherForKill(int etherForKill) {
        this.etherForKill = etherForKill;
    }

    public int getEtherForCollector() {
        return etherForCollector;
    }

    public void setEtherForCollector(int etherForCollector) {
        this.etherForCollector = etherForCollector;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }
}
