package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;

import java.util.Iterator;
import java.util.List;

public class CustomLightning extends EntityLightning {
	public CustomLightning (World world, double d0, double d1, double d2, boolean flag) {
		super(world, d0, d1, d2, flag);
	}

	public CustomLightning (World world, double d0, double d1, double d2, boolean isEffect, boolean isSilent) {
		super(world, d0, d1, d2, isEffect, isSilent);
	}
	public void B_() {
		if (!this.world.isClientSide) {
			this.setFlag(6, this.aW());
		}

		this.Y();
		int i;
		if (!this.isSilent && this.lifeTicks == 2) {
			float pitch = 0.8F + this.random.nextFloat() * 0.2F;
			i = this.world.getServer().getViewDistance() * 16;
			Iterator var3 = this.world.players.iterator();

			while(var3.hasNext()) {
				EntityPlayer player = (EntityPlayer)var3.next();
				double deltaX = this.locX - player.locX;
				double deltaZ = this.locZ - player.locZ;
				double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
				if (distanceSquared > (double)(i * i)) {
					double deltaLength = Math.sqrt(distanceSquared);
					double relativeX = player.locX + deltaX / deltaLength * (double)i;
					double relativeZ = player.locZ + deltaZ / deltaLength * (double)i;
					player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.dK, SoundCategory.WEATHER, relativeX, this.locY, relativeZ, 10000.0F, pitch));
				} else {
					player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.dK, SoundCategory.WEATHER, this.locX, this.locY, this.locZ, 10000.0F, pitch));
				}
			}

			this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.dJ, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		--this.lifeTicks;
		if (this.lifeTicks < 0) {
			if (this.c == 0) {
				this.die();
			} else if (this.lifeTicks < -this.random.nextInt(10)) {
				--this.c;
				this.lifeTicks = 1;
				if (!this.d && !this.world.isClientSide) {
					this.a = this.random.nextLong();
					BlockPosition blockposition = new BlockPosition(this);
					if (this.world.getGameRules().getBoolean("doFireTick") && this.world.areChunksLoaded(blockposition, 10) && this.world.getType(blockposition).getMaterial() == Material.AIR && Blocks.FIRE.canPlace(this.world, blockposition) && !this.isEffect && !CraftEventFactory.callBlockIgniteEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
						this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
					}
				}
			}
		}

		if (this.lifeTicks >= 0 && !this.isEffect && !this.world.isClientSide && !this.d) {
			List list = this.world.getEntities(this, new AxisAlignedBB(this.locX - 3.0D, this.locY - 3.0D, this.locZ - 3.0D, this.locX + 3.0D, this.locY + 6.0D + 3.0D, this.locZ + 3.0D));

			for(i = 0; i < list.size(); ++i) {
				Entity entity = (Entity)list.get(i);
				entity.onLightningStrike(this);
			}
		}

	}
}
