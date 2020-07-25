package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.stats.GameStatsUtils;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RewardManager {
    private HashMap<String, Reward> rewards = new HashMap<>();

    public Reward get(String name) {
        return rewards.get(name);
    }

    public void give(String reward, Player player) {
        give(reward, player, getMessage(reward));
    }

    public void give(String reward, Player player, String message) {
        int count = get(reward).getCount();
        String name = player.getName();
        GamePlayer gp = GameUtils.getPlayer(name);
        Minigame.getInstance().getGame().getGameStatsManager().addExp(gp, count);
        Plugin.getInstance().getPlayerData().increaseBalance(name, count);
        if (!message.isEmpty()) {
            player.sendMessage(message.replace("%count%", String.valueOf(count)).replace(
                    "%allcount%", String.valueOf(Game.getInstance().getStats().getPlayers().get(name).getExpGained())));
        }
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
