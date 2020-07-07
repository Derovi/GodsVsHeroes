package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityDragonFireball;
import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DFireball extends EntityDragonFireball {
    private GamePlayer owner;
    private int explodeDamage;

    public DFireball(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    public void a(MovingObjectPosition var1) {
        if (var1.entity == null || !var1.entity.s(this.shooter)) {
            if (!this.world.isClientSide) {
                final Location loc = new Location(owner.getPlayer().getWorld(), locX, locY, locZ);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
                for (final LivingEntity ent : GameUtils.getNearby(loc, 3)) {
                    if (GameUtils.isEnemy(ent, owner.getPlayer())) {
                        GameUtils.damage(10, ent, owner.getPlayer());
                    }
                }
                this.die();
            }
        }
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public int getExplodeDamage() {
        return explodeDamage;
    }

    public void setExplodeDamage(int explodeDamage) {
        this.explodeDamage = explodeDamage;
    }

    public GamePlayer getOwner() {
        return owner;
    }

    public void setOwner(GamePlayer owner) {
        this.owner = owner;
    }
}
