
package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AirLeapInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class AirLeap extends Item implements PlayerInteractInterface {
    private final double force;
    public AirLeap(String name, int level, Player owner) {
        super(name, level, owner);
        force = ((AirLeapInfo)getInfo()).getForce();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.setVelocity(player.getLocation().getDirection().multiply(force));
    }
}