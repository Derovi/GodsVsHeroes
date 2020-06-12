package by.dero.gvh.model.interfaces;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileHitInterface {
    void onProjectileHit(ProjectileHitEvent event);
    void onProjectileHitEnemy(ProjectileHitEvent event);
}
