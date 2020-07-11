package by.dero.gvh.nmcapi.dragon;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class RotatingDragon extends EntityEnderDragon {
    private final ControlledDragon dragon;

    public RotatingDragon(ControlledDragon dragon) {
        super(((CraftWorld) dragon.getPlayer().getWorld()).getHandle());
        setPosition(dragon.getPlayer().getLocation().getX(),
                dragon.getPlayer().getLocation().getY(),
                dragon.getPlayer().getLocation().getZ());
        this.dragon = dragon;
        setHealth(20);
    }

    @Override
    public void r() {}

    @Override
    public void B_() {
        dg();
    }

    public void setRotation(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public void dg() {
        if (this.currentEnderCrystal != null) {
            if (this.currentEnderCrystal.dead) {
                this.currentEnderCrystal = null;
            } else if (this.ticksLived % 15 == 0) {
                // !TODO dragon crystal reaction
                //dragon.getPlayer().setHealth(dragon.getPlayer().getHealth() - 1);
            }
        }

        if (this.ticksLived % 10 == 0) {
            List<EntityEnderCrystal> list = this.world.a(EntityEnderCrystal.class, this.getBoundingBox().g(32.0D));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = 1.7976931348623157E308D;
            int i1 = 0;

            for(int listSize = list.size(); i1 < listSize; ++i1) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal)list.get(i1);
                double d1 = entityendercrystal1.h(this);
                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }
            this.currentEnderCrystal = entityendercrystal;
        }
    }

    public void spawn() {
        getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
