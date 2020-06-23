
package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AirLeapInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.model.Drawings.drawCircleInFront;

public class AirLeap extends Item implements PlayerInteractInterface {
    private final double force;
    public AirLeap(String name, int level, Player owner) {
        super(name, level, owner);
        force = ((AirLeapInfo) getInfo()).getForce();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawCircleInFront(player, 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        player.setVelocity(player.getLocation().getDirection().multiply(force));
    }
}