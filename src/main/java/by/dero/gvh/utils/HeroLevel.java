package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.PlayerInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class HeroLevel {
    private int exp;
    private int level;

    public HeroLevel(PlayerInfo info, String hero) {
        this.exp = 0;
        Map<String, Integer> items = info.getClasses().getOrDefault(hero, null);
        if (items == null) {
            exp = 0;
            level = 0;
            return;
        }
        int[] fullLevel = new int[6];
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            int current = 0;
            for (ItemInfo itemInfo :
                    Plugin.getInstance().getData().getItems().get(entry.getKey()).getLevels()) {
                if (current > 0) {
                    fullLevel[current] += itemInfo.getCost();
                    if (current <= entry.getValue()) {
                        exp += itemInfo.getCost();
                    }
                }
                ++current;
            }
        }
        int[] sum = new int[6];
        for (int idx = 1; idx < 6; ++idx) {
            sum[idx] = sum[idx - 1] + fullLevel[idx];
;       }
        if (exp < fullLevel[1]) {
            level = 1;
        } else if (exp < sum[1] + fullLevel[2] / 2) {
            level = 2;
        } else if (exp < sum[2]) {
            level = 3;
        } else if (exp < sum[2] + fullLevel[3] / 2) {
            level = 4;
        } else if (exp < sum[3]) {
            level = 5;
        } else if (exp < sum[3] + fullLevel[4] / 2) {
            level = 6;
        } else if (exp < sum[4]) {
            level = 7;
        } else if (exp < sum[4] + fullLevel[5] / 2) {
            level = 8;
        } else if (exp < sum[5]) {
            level = 9;
        } else {
            level = 10;
        }
    }

    public String getRomeLevel() {
        return GameUtils.romeNumbers[level];
    }
}
