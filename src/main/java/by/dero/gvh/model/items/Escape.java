package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.EscapeInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Escape extends Item implements PlayerInteractInterface {
    private final double force;
    private final double damage;
    public Escape(final String name, final int level, final Player owner) {
        super(name, level, owner);
        EscapeInfo info = (EscapeInfo) getInfo();
        force = info.getForce();
        damage = info.getDamage();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, owner.getLocation(), 1);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
        for (final LivingEntity entity : GameUtils.getNearby(owner.getLocation(), 3)) {
            if (GameUtils.isEnemy(entity, getTeam())) {
                GameUtils.damage(damage, entity, owner);
            }
        }
        owner.setVelocity(owner.getLocation().getDirection().multiply(-force));
    }
}
