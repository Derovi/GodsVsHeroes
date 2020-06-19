package by.dero.gvh.minigame;

import java.util.HashMap;

public class RewardManager {
    private final HashMap<String, Reward> rewards = new HashMap<>();

    public Reward get(String name) {
        return rewards.get(name);
    }

    public HashMap<String, Reward> getRewards() {
        return rewards;
    }
}
