package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.EagleFlyInfo;
import by.dero.gvh.nmcapi.ChickenAvatar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EagleFly extends Item implements DoubleSpaceInterface {
    private final EagleFlyInfo info;

    public EagleFly(final String name, final int level, final Player owner) {
        super(name, level, owner);
        info = (EagleFlyInfo) getInfo();
    }

    @Override
    public void onDoubleSpace() {
        ChickenAvatar passiveChicken = new ChickenAvatar(owner);
        passiveChicken.spawn();
        new BukkitRunnable() {
            @Override
            public void run() {
                passiveChicken.finish();
            }
        }.runTaskLater(Plugin.getInstance(), info.getDuration());
        passiveChicken.setSpeed(0.06);
        passiveChicken.getBukkitEntity().setVelocity(new Vector(0,1,0));
        new BukkitRunnable() {
            @Override
            public void run() {
                passiveChicken.setSpeed(info.getSpeed());
            }
        }.runTaskLater(Plugin.getInstance(), 25);
    }
}
