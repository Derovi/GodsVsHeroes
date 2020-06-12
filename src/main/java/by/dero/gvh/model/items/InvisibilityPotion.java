package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.InvisibilityPotionInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityPotion extends Item implements PlayerInteractInterface {
    private final int duration;
    public InvisibilityPotion(String name, int level, Player owner) {
        super(name, level, owner);
        duration = ((InvisibilityPotionInfo)getInfo()).getDuration();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1).apply(event.getPlayer());
        event.setCancelled(true);
    }
}
