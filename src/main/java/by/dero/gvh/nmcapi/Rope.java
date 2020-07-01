package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.*;

public class Rope extends EntityFishingHook {
    private int lifeTime;

    public Rope(World world, EntityHuman entityhuman, double stMotX, double stMotY, double stMotZ, int lifeTime) {
        super(world, entityhuman);
    }

    @Override
    public void B_() {
        if (!this.world.isClientSide) {
            this.setFlag(6, this.aW());
        }
        this.Y();

        if (--lifeTime < 0 || this.owner == null) {
            this.die();
        } else if (this.world.isClientSide || !this.p()) {
            if (this.isInGround) {
                ++this.d;
                if (this.d >= 120) {
                    this.die();
                    return;
                }
            }

            float f = 0.0F;
            BlockPosition blockposition = new BlockPosition(this);
            IBlockData iblockdata = this.world.getType(blockposition);
            if (iblockdata.getMaterial() == Material.WATER) {
                f = BlockFluids.g(iblockdata, this.world, blockposition);
            }

            if (this.av == EntityFishingHook.HookState.FLYING) {
                if (this.hooked != null) {
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                    this.av = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (f > 0.0F) {
                    this.motX *= 0.3D;
                    this.motY *= 0.2D;
                    this.motZ *= 0.3D;
                    this.av = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                if (!this.world.isClientSide) {
                    this.r();
                }

                if (!this.isInGround && !this.onGround && !this.positionChanged) {
                    ++this.f;
                } else {
                    this.f = 0;
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                }
            } else {
                if (this.av == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hooked != null) {
                        if (this.hooked.dead) {
                            this.hooked = null;
                            this.av = EntityFishingHook.HookState.FLYING;
                        } else {
                            this.locX = this.hooked.locX;
                            double d1 = (double)this.hooked.length;
                            this.locY = this.hooked.getBoundingBox().b + d1 * 0.8D;
                            this.locZ = this.hooked.locZ;
                            this.setPosition(this.locX, this.locY, this.locZ);
                        }
                    }

                    return;
                }

                if (this.av == EntityFishingHook.HookState.BOBBING) {
                    this.motX *= 0.9D;
                    this.motZ *= 0.9D;
                    double d0 = this.locY + this.motY - (double)blockposition.getY() - (double)f;
                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.motY -= d0 * (double)this.random.nextFloat() * 0.2D;
                    if (!this.world.isClientSide && f > 0.0F) {
                        this.a(blockposition);
                    }
                }
            }

            if (iblockdata.getMaterial() != Material.WATER) {
                this.motY -= 0.03D;
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.q();
            this.motX *= 0.92D;
            this.motY *= 0.92D;
            this.motZ *= 0.92D;
            this.setPosition(this.locX, this.locY, this.locZ);
            if (this.inPortal()) {
                this.die();
            }
        }
    }

    @Override
    public boolean p() {
        return true;
    }
}
