package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RewardManager {
    private HashMap<String, Reward> rewards = new HashMap<>();
    private final HashMap<String, Double> playerExp = new HashMap<>();

    public Reward get(String name) {
        return rewards.get(name);
    }

    public void give(String reward, Player player) {
        give(reward, player, getMessage(reward));
    }

    public void give(String reward, Player player, String message) {
        String name = player.getName();
        GamePlayer gp = GameUtils.getPlayer(name);
        double mult = Game.getInstance().getBoosterMult().getOrDefault(gp, -1.0);
        if (mult == -1) {
            mult = Plugin.getInstance().getBoosterManager().calculateMultiplier(Game.getInstance(), gp);
            Game.getInstance().getBoosterMult().put(gp, mult);
        }

        double count = mult * get(reward).getCount();
        Minigame.getInstance().getGame().getGameStatsManager().addExp(gp, count);

        double currentExp = playerExp.getOrDefault(name, 0.0);
        playerExp.put(name, currentExp + count);

        if (!message.isEmpty()) {
            player.sendMessage(message.replace("%count%", GameUtils.getString(count)).replace(
                    "%allcount%", GameUtils.getString(Game.getInstance().getStats().getPlayers().get(name).getExpGained())));
        }
    }

    public void addExp(String name, double val) {
        playerExp.put(name, playerExp.getOrDefault(name, 0.0) + val);
    }
    
    public int getExp(String playerName) {
        return (int) (double) playerExp.getOrDefault(playerName, 0.0);
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
