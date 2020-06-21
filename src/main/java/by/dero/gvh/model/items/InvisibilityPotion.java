package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.InvisibilityPotionInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(Plugin.getInstance(), event.getPlayer());
        }
        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(Plugin.getInstance(), event.getPlayer());
            }
        }, duration);
    }


}
