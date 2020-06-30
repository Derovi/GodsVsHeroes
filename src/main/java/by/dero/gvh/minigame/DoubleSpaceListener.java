package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;

import static by.dero.gvh.utils.GameUtils.getPlayer;

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
        if (getItems(getPlayer(player.getName())).isEmpty()) {
            return;
        }

        final Block block = player.getLocation().clone().subtract(0,1,0).getBlock();;
        if (block.getType ().isSolid ()) {
            player.setAllowFlight (true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage (final EntityDamageEvent event) {
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            if (event.getEntityType () == EntityType.PLAYER &&
                    !getItems(getPlayer(event.getEntity().getName())).isEmpty() &&
                    event.getCause () == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled (true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerMove (final PlayerMoveEvent event) {
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            final Player p = event.getPlayer();
            if (!getItems(getPlayer(p.getName())).isEmpty() && !p.getAllowFlight()) {
                groundUpdate(p);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            final Player p = event.getPlayer();
            final ArrayList<DoubleSpaceInterface> items = getItems(getPlayer(p.getName()));
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
