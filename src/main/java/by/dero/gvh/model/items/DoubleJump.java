package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static by.dero.gvh.model.Drawings.drawCircleInFront;

public class DoubleJump extends Item implements DoubleSpaceInterface {
    public DoubleJump(final String name, final int level, final Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onDoubleSpace(Player player) {
        drawCircleInFront(player.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        player.setVelocity(player.getLocation().getDirection().multiply (1.1d).setY (1.0d));
    }
}
