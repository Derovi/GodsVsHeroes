package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.isEnemy;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class StunRocks extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, PlayerInteractInterface, ProjectileLaunchInterface {
    private final int duration;
    public StunRocks(final String name, final int level, final Player owner) {
        super(name, level, owner);
        duration = ((StunRocksInfo) getInfo()).getDuration();
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        if (isEnemy(event.getHitEntity(), getTeam())) {
            Stun.stunEntity((LivingEntity) event.getHitEntity(), duration);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

    }
}

