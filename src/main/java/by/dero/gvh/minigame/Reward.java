package by.dero.gvh.minigame;

import by.dero.gvh.Plugin;
import org.bukkit.entity.Player;

public class Reward {
    private String message;
    private int count;

    public void give(Player player) {
        give(player, message);
    }

    public void give(Player player, String message) {
        Plugin.getInstance().getPlayerData().increaseBalance(player.getName(), count);
        player.sendMessage(message.replace("%count%", String.valueOf(count)));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }
}
