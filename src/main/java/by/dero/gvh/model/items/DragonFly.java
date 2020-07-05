package by.dero.gvh.model.items;

import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.Item;
import by.dero.gvh.nmcapi.dragon.ControlledDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class DragonFly extends Item implements PlayerInteractInterface {
    public DragonFly(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        ControlledDragon dragon = new ControlledDragon(owner);
    }
}
