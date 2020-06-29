package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.ForceLeapInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ForceLeap extends Item implements DoubleSpaceInterface {
    private final double force;
    private final int duration;
    public ForceLeap(String name, int level, Player owner) {
        super(name, level, owner);
        ForceLeapInfo info = (ForceLeapInfo) getInfo();
        force = info.getForce();
        duration = info.getDuration();
    }

    @Override
    public void onDoubleSpace() {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        Drawings.drawCircleInFront(owner.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        owner.setVelocity(owner.getLocation().getDirection().multiply(force));
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
    }
}
