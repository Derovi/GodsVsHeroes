package by.dero.gvh.minigame;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RewardManager {
    private HashMap<String, Reward> rewards = new HashMap<>();
    private final HashMap<String, Integer> xpEarned = new HashMap<>();

    public Reward get(String name) {
        return rewards.get(name);
    }

    public void give(String reward, Player player) {
        give(reward, player, getMessage(reward));
    }

    public void give(String reward, Player player, String message) {
        int count = get(reward).getCount();
        xpEarned.put(player.getName(), xpEarned.getOrDefault(player.getName(), 0) + count);
        Plugin.getInstance().getPlayerData().increaseBalance(player.getName(), count);
        player.sendMessage(message.replace("%count%", String.valueOf(count)).replace(
                "%allcount%", String.valueOf(xpEarned.get(player.getName()))));
    }

    public String getMessage(String reward) {
        return rewards.get(reward).getMessage();
    }

    public void setRewards(HashMap<String, Reward> rewards) {
        this.rewards = rewards;
    }

    public HashMap<String, Reward> getRewards() {
        return rewards;
    }
}
