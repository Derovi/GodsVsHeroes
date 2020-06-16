package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.GrenadeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class Grenade extends Item implements InfiniteReplenishInterface,
        PlayerInteractInterface, ProjectileHitInterface, ProjectileLaunchInterface {
    private final double force;
    public Grenade(final String name, final int level, final Player owner) {
        super(name, level, owner);
        force = ((GrenadeInfo) getInfo()).getForce();
    }


    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().createExplosion(loc, (float) force);
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

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
}
