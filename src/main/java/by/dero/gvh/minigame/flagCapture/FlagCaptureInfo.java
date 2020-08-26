package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.utils.IntPosition;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FlagCaptureInfo {
	private IntPosition[] flagPoints;
	private int flagsToWin;
}
