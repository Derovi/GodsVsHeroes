package by.dero.gvh.minigame;

import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class RewardManager {
    private HashMap<String, Reward> rewards = new HashMap<>();

    public Reward get(String name) {
        return rewards.get(name);
    }

    public static void main(String[] args) {
        RewardManager manager = new RewardManager();
        Reward reward = new Reward();
        reward.setCount(4);
        reward.setMessage("mes");
        manager.getRewards().put("killEnemy", reward);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(manager.getRewards()));
    }

    public void setRewards(HashMap<String, Reward> rewards) {
        this.rewards = rewards;
    }

    public HashMap<String, Reward> getRewards() {
        return rewards;
    }
}
