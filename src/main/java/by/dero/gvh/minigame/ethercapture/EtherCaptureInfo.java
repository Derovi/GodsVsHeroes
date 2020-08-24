package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.utils.IntPosition;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EtherCaptureInfo {
    private IntPosition[] etherCollectors;
    private int etherToWin;
    private int etherForKill;
    private int etherForCollector;
    private int etherMineDelay;
}
