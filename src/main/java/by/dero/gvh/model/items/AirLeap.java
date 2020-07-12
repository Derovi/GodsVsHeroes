
package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AirLeapInfo;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class AirLeap extends Item implements PlayerInteractInterface {
    private final double force;
    public AirLeap(String name, int level, Player owner) {
        super(name, level, owner);
        force = ((AirLeapInfo) getInfo()).getForce();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.getWorld().playEffect(owner.getLocation(), Effect.WITHER_SHOOT, null);
        Drawings.drawCircleInFront(owner.getEyeLocation(), 3, 0.5, Particle.EXPLOSION_LARGE);
        owner.setVelocity(owner.getLocation().getDirection().multiply(force));
    }
}