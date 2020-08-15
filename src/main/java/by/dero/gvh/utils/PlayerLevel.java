package by.dero.gvh.utils;

import lombok.Getter;

@Getter
public class PlayerLevel {
    private final int exp;
    private final int level;
    private final int expOnThisLevel;
    private final int expToNextLevel;

    public PlayerLevel(int exp) {
        this.exp = exp;
        int delta = 800;
        int startExp = 0;
        for (int level = 1; ; ++level) {
            startExp += delta;
            if (level % 40 == 0) {
                delta += 100;
            }
            if (startExp > exp) {
                this.level = level;
                this.expOnThisLevel = exp - startExp + delta;
                this.expToNextLevel = delta;
                return;
            }
        }
    }
}
