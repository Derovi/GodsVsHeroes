package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

public class InfiniteFishHook extends EntityFishingHook {
	public InfiniteFishHook (World world, EntityHuman entityhuman) {
		super(world, entityhuman);
	}

	public void B_() {
		super.B_();
		if (this.owner == null) {
			this.die();
		} else if (this.world.isClientSide || !this.p()) {
			if (this.isInGround) {
				++this.d;
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

	public boolean p() {
		ItemStack itemstack = this.owner.getItemInMainHand();
		ItemStack itemstack1 = this.owner.getItemInOffHand();
		boolean flag = itemstack.getItem() == Items.FISHING_ROD;
		boolean flag1 = itemstack1.getItem() == Items.FISHING_ROD;
		return this.owner.dead || !this.owner.isAlive() || (!flag && !flag1) || !(this.h(this.owner) <= 1024.0D);
	}


	public int j() {
		if (!this.world.isClientSide && this.owner != null) {
			int i = 0;
			PlayerFishEvent playerFishEvent;
			if (this.hooked != null) {
				playerFishEvent = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), this.hooked.getBukkitEntity(), (Fish)this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
				this.world.getServer().getPluginManager().callEvent(playerFishEvent);
				if (playerFishEvent.isCancelled()) {
					return 0;
				}

				this.k();
				this.world.broadcastEntityEffect(this, (byte)31);
				i = this.hooked instanceof EntityItem ? 3 : 5;
			} else if (this.g > 0) {
				LootTableInfo.a loottableinfo_a = new LootTableInfo.a((WorldServer)this.world);
				loottableinfo_a.a((float)this.aw + this.owner.du());
				List<ItemStack> a = this.world.getLootTableRegistry().a(LootTables.aA).a(this.random, loottableinfo_a.a());
				int i1 = 0;
				int aSize = a.size();

				while(true) {
					if (i1 >= aSize) {
						i = 1;
						break;
					}

					ItemStack itemstack = (ItemStack)a.get(i1);
					EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);
					playerFishEvent = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), entityitem.getBukkitEntity(), (Fish)this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
					playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
					this.world.getServer().getPluginManager().callEvent(playerFishEvent);
					if (playerFishEvent.isCancelled()) {
						return 0;
					}

					double d0 = this.owner.locX - this.locX;
					double d1 = this.owner.locY - this.locY;
					double d2 = this.owner.locZ - this.locZ;
					double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					entityitem.motX = d0 * 0.1D;
					entityitem.motY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
					entityitem.motZ = d2 * 0.1D;
					this.world.addEntity(entityitem);
					if (playerFishEvent.getExpToDrop() > 0) {
						this.owner.world.addEntity(new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5D, this.owner.locZ + 0.5D, playerFishEvent.getExpToDrop(), ExperienceOrb.SpawnReason.FISHING, this.owner, this));
					}

					Item item = itemstack.getItem();
					if (item == Items.FISH || item == Items.COOKED_FISH) {
						this.owner.a(StatisticList.E, 1);
					}

					++i1;
				}
			}

			if (this.isInGround) {
				playerFishEvent = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), (org.bukkit.entity.Entity)null, (Fish)this.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
				this.world.getServer().getPluginManager().callEvent(playerFishEvent);
				if (playerFishEvent.isCancelled()) {
					return 0;
				}

				i = 2;
			}

			if (i == 0) {
				playerFishEvent = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), (org.bukkit.entity.Entity)null, (Fish)this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
				this.world.getServer().getPluginManager().callEvent(playerFishEvent);
				if (playerFishEvent.isCancelled()) {
					return 0;
				}
			}

			return i;
		} else {
			return 0;
		}
	}
}
