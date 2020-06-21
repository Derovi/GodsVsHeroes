package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.EscapeInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Escape extends Item implements PlayerInteractInterface {
    private final double force;
    public Escape(final String name, final int level, final Player owner) {
        super(name, level, owner);
        force = ((EscapeInfo) getInfo()).getForce();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
        player.setVelocity(player.getLocation().getDirection().multiply(-force));
    }
}
