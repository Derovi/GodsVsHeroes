package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.DragonFlyInfo;
import by.dero.gvh.nmcapi.dragon.ControlledDragon;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonFly extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final DragonFlyInfo info;

    public DragonFly(String name, int level, Player owner) {
        super(name, level, owner);
        info = (DragonFlyInfo) getInfo();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 100, 1);
        ControlledDragon dragon = new ControlledDragon(owner);
        dragon.setSpeed(info.getSpeed());
        new BukkitRunnable() {
            @Override
            public void run() {
                dragon.finish();
            }
        }.runTaskLater(Plugin.getInstance(), info.getDuration());
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack result = super.getItemStack();
        if (result.getType().equals(Material.SKULL_ITEM)) {
            result.setDurability((short) 5);
        }
        return result;
    }
}
