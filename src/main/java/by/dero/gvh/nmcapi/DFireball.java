package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.utils.GameUtils;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DFireball extends EntityDragonFireball {
    private GamePlayer owner;
    private double explodeDamage;

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

    public void setDirection(double d0, double d1, double d2) {
        System.out.println("Direction!");
        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        this.dirX = d0 / d3 * 0.1D;
        this.dirY = d1 / d3 * 0.1D;
        this.dirZ = d2 / d3 * 0.1D;
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public double getExplodeDamage() {
        return explodeDamage;
    }

    public void setExplodeDamage(double explodeDamage) {
        this.explodeDamage = explodeDamage;
    }

    public GamePlayer getOwner() {
        return owner;
    }

    public void setOwner(GamePlayer owner) {
        this.owner = owner;
    }
}
