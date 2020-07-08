package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DoubleJump extends Item implements DoubleSpaceInterface {
    public DoubleJump(final String name, final int level, final Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onDoubleSpace() {
        Drawings.drawCircleInFront(owner.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
        owner.setVelocity(owner.getLocation().getDirection().multiply (1.1d).setY (1.0d));
    }
}
