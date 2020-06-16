package by.dero.gvh.model.interfaces;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileHitInterface {
    void onProjectileHit(final ProjectileHitEvent event);
    void onProjectileHitEnemy(final ProjectileHitEvent event);
}
