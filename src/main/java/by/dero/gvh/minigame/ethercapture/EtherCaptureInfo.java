package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.utils.IntPosition;
import by.dero.gvh.utils.Position;

public class EtherCaptureInfo {
    private IntPosition[] etherCollectors;
    private int etherToWin;
    private int etherForKill;
    private int etherForCollector;
    private int captureTime;

    public IntPosition[] getEtherCollectors() {
        return etherCollectors;
    }

    public void setEtherCollectors(IntPosition[] etherCollectors) {
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
