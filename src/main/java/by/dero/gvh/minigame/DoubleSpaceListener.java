package by.dero.gvh.minigame;

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
    private void groundUpdate (final Player player) {
        if (GameUtils.selectItems(GameUtils.getPlayer(player.getName()), DoubleSpaceInterface.class).isEmpty()) {
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
            if (!GameUtils.selectItems(GameUtils.getPlayer(p.getName()), DoubleSpaceInterface.class).isEmpty() &&
                    !p.getAllowFlight()) {
                groundUpdate(p);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        event.setCancelled(true);
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            final Player p = event.getPlayer();
            final ArrayList<DoubleSpaceInterface> items = GameUtils.selectItems(
                    GameUtils.getPlayer(p.getName()), DoubleSpaceInterface.class);
            if (!items.isEmpty() && p.getGameMode() == GameMode.SURVIVAL) {
                p.setAllowFlight(false);
                for (final DoubleSpaceInterface item : items) {
                    item.onDoubleSpace();
                }
            }
        }
    }
}
