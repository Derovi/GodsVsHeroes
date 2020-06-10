package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InteractInterface;
import by.dero.gvh.model.itemsinfo.AirLeapInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class AirLeap extends Item implements InteractInterface {
    public AirLeap(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.setVelocity(player.getLocation().getDirection().multiply(((AirLeapInfo)getInfo()).getForce()));
    }
}
