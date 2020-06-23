package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class FlyBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
    public FlyBow(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerShootBow(EntityShootBowEvent event) {
        final Player player = (Player) event.getEntity();
        if (!cooldown.isReady()) {
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
        final double modifier = ((FlyBowInfo) getInfo()).getModifier();
        player.setVelocity(new Vector(event.getProjectile().getVelocity().getX() * modifier,
                event.getProjectile().getVelocity().getY() * modifier,
                event.getProjectile().getVelocity().getZ() * modifier));
        event.getProjectile().remove();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}
