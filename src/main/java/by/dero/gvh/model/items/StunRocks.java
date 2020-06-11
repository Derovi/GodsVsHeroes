package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StunRocks extends Item implements InfiniteReplenishInterface, ProjectileHitInterface, PlayerInteractInterface {
    private final int duration;
    public StunRocks(String name, int level, Player owner) {
        super(name, level, owner);
        duration = ((StunRocksInfo)getInfo()).getDuration();
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        Plugin.getStunAPI().add(event.getHitEntity().getUniqueId(), duration);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }
}
