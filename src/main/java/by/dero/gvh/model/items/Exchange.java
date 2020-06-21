package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExchangeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Exchange extends Item implements PlayerInteractInterface {
    private final double maxRange;
    public Exchange(String name, int level, Player owner) {
        super(name, level, owner);
        maxRange = ((ExchangeInfo) getInfo()).getMaxRange();
    }

    public Entity getTargetEntity(final Entity entity) {
        return getTarget(entity, entity.getWorld().getLivingEntities());
    }

    public <T extends Entity> T getTarget(final Entity entity,
                                                 final Iterable<T> entities) {
        if (entity == null)
            return null;
        T target = null;
        final double threshold = 1;
        for (final T other : entities) {
            if (other.getLocation().distance(entity.getLocation()) > maxRange) {
                continue;
            }
            final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (entity.getLocation().getDirection().normalize().crossProduct(n)
                    .lengthSquared() < threshold &&
                    n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().distanceSquared(
                        entity.getLocation()) > other.getLocation()
                        .distanceSquared(entity.getLocation())) {
                    target = other;
                }
            }
        }
        return target;
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Entity target = getTargetEntity(player);

        final Location zxc = player.getLocation().clone();
        if (target != null) {
            if (!cooldown.isReady()) {
                return;
            }
            cooldown.reload();
            player.teleport(target);
            target.teleport(zxc);
        }
    }
}
