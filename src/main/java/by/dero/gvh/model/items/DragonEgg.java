package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.DragonEggInfo;
import by.dero.gvh.nmcapi.ChickenAvatar;
import by.dero.gvh.nmcapi.DragonEggEntity;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonEgg extends Item implements DoubleSpaceInterface {
    private final DragonEggInfo info;

    public DragonEgg(String name, int level, Player owner) {
        super(name, level, owner);
        info = (DragonEggInfo) getInfo();
    }

    @Override
    public void onDoubleSpace() {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        DragonEggEntity egg = new DragonEggEntity(owner);
        // TODO add sound
        // TODO add status bar
        egg.spawn();
        new BukkitRunnable() {
            @Override
            public void run() {
                egg.finish();
            }
        }.runTaskLater(Plugin.getInstance(), info.getDuration());
    }
}
