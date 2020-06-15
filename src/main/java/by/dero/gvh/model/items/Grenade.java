package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.GrenadeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Grenade extends Item implements InfiniteReplenishInterface, PlayerInteractInterface, ProjectileHitInterface {
    private final double force;
    public Grenade(String name, int level, Player owner) {
        super(name, level, owner);
        GrenadeInfo info = (GrenadeInfo) getInfo();
        force = info.getForce();
    }


    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity proj = event.getEntity();
        Location loc = proj.getLocation();
        loc.getWorld().createExplosion(loc, (float) force);
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }
}
