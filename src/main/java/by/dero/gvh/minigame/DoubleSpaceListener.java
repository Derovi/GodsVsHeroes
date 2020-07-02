package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;

public class DoubleSpaceListener implements Listener {
    private ArrayList<DoubleSpaceInterface> getItems(final GamePlayer gp) {
        final ArrayList<DoubleSpaceInterface> list = new ArrayList<>();
        for (final Item item : gp.getItems().values()) {
            if (item instanceof DoubleSpaceInterface) {
                list.add((DoubleSpaceInterface) item);
            }
        }
        return list;
    }

    private void groundUpdate (final Player player) {
        if (getItems(GameUtils.getPlayer(player.getName())).isEmpty()) {
            return;
        }

        final Block block = player.getLocation().clone().subtract(0,1,0).getBlock();;
        if (block.getType ().isSolid ()) {
            player.setAllowFlight (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerMove (final PlayerMoveEvent event) {
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            final Player p = event.getPlayer();
            if (!getItems(GameUtils.getPlayer(p.getName())).isEmpty() && !p.getAllowFlight()) {
                groundUpdate(p);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            final Player p = event.getPlayer();
            final ArrayList<DoubleSpaceInterface> items = getItems(GameUtils.getPlayer(p.getName()));
            if (!items.isEmpty() && p.getGameMode() == GameMode.SURVIVAL) {
                p.setAllowFlight(false);
                event.setCancelled(true);
                for (final DoubleSpaceInterface item : items) {
                    item.onDoubleSpace();
                }
            }
        }
    }
}
