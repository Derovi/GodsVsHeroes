package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.EscapeInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Escape extends Item implements PlayerInteractInterface {
    private final double force;
    public Escape(String name, int level, Player owner) {
        super(name, level, owner);
        force = ((EscapeInfo) getInfo()).getForce();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        p.setVelocity(p.getLocation().getDirection().multiply(-force));
    }
}
