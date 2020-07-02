package by.dero.gvh.minigame;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.SneakInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SneakListener implements Listener {
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        for (Item item : GameUtils.getPlayer(player.getName()).getItems().values()) {
            if (item instanceof SneakInterface) {
                ((SneakInterface) item).onPlayerSneak();
            }
        }
    }
}
