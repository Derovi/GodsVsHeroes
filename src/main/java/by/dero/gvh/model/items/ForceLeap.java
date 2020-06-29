package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AirLeapInfo;
import by.dero.gvh.model.itemsinfo.ForceLeapInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static by.dero.gvh.model.Drawings.drawCircleInFront;

public class ForceLeap extends Item implements PlayerInteractInterface {
    private final double force;
    private final int duration;
    public ForceLeap(String name, int level, Player owner) {
        super(name, level, owner);
        ForceLeapInfo info = (ForceLeapInfo) getInfo();
        force = info.getForce();
        duration = info.getDuration();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawCircleInFront(player.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        player.setVelocity(player.getLocation().getDirection().multiply(force));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
    }
}
