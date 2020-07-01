package by.dero.gvh.minigame;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.SneakInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.UUID;

public class SneakListener implements Listener {
//    private final HashMap<UUID, Long> lastSneak = new HashMap<>();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
//        Long now = System.currentTimeMillis();
//        UUID uuid = player.getUniqueId();
//        if (now - lastSneak.getOrDefault(uuid, 0L) < 500) {
//            lastSneak.remove(uuid);
            for (Item item : GameUtils.getPlayer(player.getName()).getItems().values()) {
                if (item instanceof SneakInterface) {
                    ((SneakInterface) item).onPlayerSneak();
                }
            }
//        } else {
//            lastSneak.put(uuid, now);
//        }
    }
}
