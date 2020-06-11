package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

public class StunRocks extends Item implements InfiniteReplenishInterface, ProjectileHitInterface {
    public StunRocks(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) { }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        Plugin.getStunAPI().add(event.getHitEntity().getUniqueId(),
                ((StunRocksInfo)getInfo()).getDuration());
    }
}
