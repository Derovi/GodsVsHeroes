package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
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
                // EXPLODE
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
