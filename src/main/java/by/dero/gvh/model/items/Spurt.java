package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.SpurtInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Spurt extends Item implements DoubleSpaceInterface {
    private final SpurtInfo info;

    public Spurt(final String name, final int level, final Player owner) {
        super(name, level, owner);
        info = (SpurtInfo) getInfo();
    }

    @Override
    public void onDoubleSpace() {
        Drawings.drawCircleInFront(owner.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        owner.setVelocity(owner.getLocation().getDirection().normalize().multiply(info.getPower()).setY (0.3));
        float playerSpeed = owner.getWalkSpeed();
        owner.setWalkSpeed(playerSpeed * 1.3f);
        new BukkitRunnable() {
            @Override
            public void run() {
                owner.setWalkSpeed(playerSpeed);
            }
        }.runTaskLater(Plugin.getInstance(), info.getSpeedTime());
    }
}
