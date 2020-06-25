package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.InvisibilityPotionInfo;
import by.dero.gvh.utils.Invisibility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class InvisibilityPotion extends Item implements PlayerInteractInterface {
    private final int duration;
    public InvisibilityPotion(final String name, final int level, final Player owner) {
        super(name, level, owner);
        duration = ((InvisibilityPotionInfo) getInfo()).getDuration();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        Invisibility.makeInvisible(getPlayer(event.getPlayer().getName()), duration);
    }


}
