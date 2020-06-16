package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.HealPotionInfo;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.isAlly;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class HealPotion extends Item implements ProjectileHitInterface,
        InfiniteReplenishInterface, PlayerInteractInterface, ProjectileLaunchInterface {
    private final double radius;
    private final int heal;

    public HealPotion(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final HealPotionInfo info = (HealPotionInfo)getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity at = event.getEntity();
        for (final Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (isAlly(ent, team) && ent.getLocation().distance(at.getLocation()) <= radius) {
                final LivingEntity cur = (LivingEntity) ent;
                final double hp = Math.min(cur.getHealth() + heal,
                        cur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                cur.setHealth(hp);
            }
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!cooldown.isReady()) {
            sendCooldownMessage((Player) event.getEntity(),
                    getInfo().getDisplayName(), cooldown.getSecondsRemaining());
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {

    }
}
