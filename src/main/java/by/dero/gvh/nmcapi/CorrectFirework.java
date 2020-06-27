package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CorrectFirework extends EntityFireworks {
    public CorrectFirework(Location loc) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void B_() {
        this.M = this.locX;
        this.N = this.locY;
        this.O = this.locZ;
        super.B_();
        if (this.j()) {
            if (this.e == null) {
                Entity entity = this.world.getEntity((Integer)this.datawatcher.get(b));
                if (entity instanceof EntityLiving) {
                    this.e = (EntityLiving)entity;
                }
            }

            if (this.e != null) {
                if (this.e.cP()) {
                    Vec3D vec3d = this.e.aJ();
                    EntityLiving var10000 = this.e;
                    var10000.motX += vec3d.x * 0.1D + (vec3d.x * 1.5D - this.e.motX) * 0.5D;
                    var10000 = this.e;
                    var10000.motY += vec3d.y * 0.1D + (vec3d.y * 1.5D - this.e.motY) * 0.5D;
                    var10000 = this.e;
                    var10000.motZ += vec3d.z * 0.1D + (vec3d.z * 1.5D - this.e.motZ) * 0.5D;
                }

                this.setPosition(this.e.locX, this.e.locY, this.e.locZ);
                this.motX = this.e.motX;
                this.motY = this.e.motY;
                this.motZ = this.e.motZ;
            }
        } else {
            this.motX *= 1.15D;
            this.motZ *= 1.15D;
            this.motY += 0.04D;
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
        }

        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        this.yaw = (float)(MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

        for(this.pitch = (float)(MathHelper.c(this.motY, (double)f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
        }

        while(this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while(this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while(this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
        if (this.ticksFlown == 0 && !this.isSilent()) {
            this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.bI, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.ticksFlown;
        if (this.world.isClientSide) {
            this.world.addParticle(EnumParticle.FIREWORKS_SPARK, this.locX, this.locY - 0.3D, this.locZ, this.random.nextGaussian() * 0.05D, -this.motY * 0.5D, this.random.nextGaussian() * 0.05D, new int[0]);
        }

        if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
            if (!CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                this.world.broadcastEntityEffect(this, (byte)17);
                this.k();
            }
            this.die();
        }
    }
}
