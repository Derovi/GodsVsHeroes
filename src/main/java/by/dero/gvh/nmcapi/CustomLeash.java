package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class CustomLeash extends EntityLeash {
	public CustomLeash (World world) {
		super(world);
		this.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(),""));
	}
	public CustomLeash(World world, BlockPosition blockposition) {
		super(world, blockposition);
		this.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(),""));
	}

	public void setPosition(double d0, double d1, double d2) {
		super.setPosition(d0, d1, d2);
	}

	public void updateBoundingBox() {
		this.locX = (double)this.blockPosition.getX() + 0.5D;
		this.locY = (double)this.blockPosition.getY() + 0.5D;
		this.locZ = (double)this.blockPosition.getZ() + 0.5D;
		if (this.valid) {
			this.world.entityJoinedWorld(this, false);
		}

	}

	public void setDirection(EnumDirection enumdirection) {
	}

	public int getWidth() {
		return 9;
	}

	public int getHeight() {
		return 9;
	}

	public float getHeadHeight() {
		return -0.0625F;
	}

	public void a(@Nullable Entity entity) {
		this.a(SoundEffects.dG, 1.0F, 1.0F);
	}

	public boolean d(NBTTagCompound nbttagcompound) {
		return false;
	}

	public void b(NBTTagCompound nbttagcompound) {
	}

	public void a(NBTTagCompound nbttagcompound) {
	}

	public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
		if (!this.world.isClientSide) {
			List list = this.world.a(EntityInsentient.class, new AxisAlignedBB(this.locX - 40.0D, this.locY - 40.0D, this.locZ - 40.0D,
					this.locX + 40.0D, this.locY + 40.0D, this.locZ + 40.0D));
			Iterator iterator = list.iterator();

			EntityInsentient entityinsentient;
			while (iterator.hasNext()) {
				entityinsentient = (EntityInsentient) iterator.next();
				if (entityinsentient.isLeashed() && entityinsentient.getLeashHolder() == entityhuman) {
					if (CraftEventFactory.callPlayerLeashEntityEvent(entityinsentient, this, entityhuman).isCancelled()) {
						((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(entityinsentient, entityinsentient.getLeashHolder()));
					} else {
						entityinsentient.setLeashHolder(this, true);
					}
				}
			}
		}
		return true;
	}

	public boolean survives() {
		return this.world.getType(this.blockPosition).getBlock() instanceof BlockFence;
	}

	public static CustomLeash a(World world, BlockPosition blockposition) {
		CustomLeash entityleash = new CustomLeash(world, blockposition);
		world.addEntity(entityleash);
		entityleash.p();
		return entityleash;
	}

	@Nullable
	public static CustomLeash b(World world, BlockPosition blockposition) {
		int i = blockposition.getX();
		int j = blockposition.getY();
		int k = blockposition.getZ();
		List list = world.a(CustomLeash.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D));
		Iterator iterator = list.iterator();

		while(iterator.hasNext()) {
			CustomLeash entityleash = (CustomLeash) iterator.next();
			if (entityleash.getBlockPosition().equals(blockposition)) {
				return entityleash;
			}
		}

		return null;
	}

	public void p() {
		this.a(SoundEffects.dH, 1.0F, 1.0F);
	}
}
