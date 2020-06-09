package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ShootBowInterface;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class FlyBow extends Item implements ShootBowInterface, ProjectileHitInterface {
    public FlyBow(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerShootBow(EntityShootBowEvent event) {
        Player player = (Player) event.getEntity();
        double modifier = ((FlyBowInfo) getInfo()).getModifier();
        player.setVelocity(new Vector(event.getProjectile().getVelocity().getX() * modifier,
                event.getProjectile().getVelocity().getY() * modifier,
                event.getProjectile().getVelocity().getZ() * modifier));
        event.getProjectile().remove();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }
}
