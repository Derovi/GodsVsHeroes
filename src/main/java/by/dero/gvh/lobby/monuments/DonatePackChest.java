package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DonatePackChest {
	@Getter @Setter
	private static int animCnt = 0;
	private final int animDuration = 320;
	private final int firstStage = 180;
	private final Location loc;
	private final EntityArmorStand[] stands = new EntityArmorStand[3];
	private final BukkitRunnable runnable;
	
	private Location getInCircle(double angle) {
		return loc.clone().add(MathUtils.cos(angle + Math.PI / 2), MathUtils.sin(angle + Math.PI / 2) - stands[0].getHeadHeight(), 0);
	}
	
	public DonatePackChest(DirectedPosition position) {
		loc = position.toLocation(Lobby.getInstance().getWorld());
		loc.getBlock().setType(Material.ENDER_CHEST);
		loc.add(0.5, 0.4375, 0.7);
		for (int i = 0; i < stands.length; i++) {
			stands[i] = new EntityArmorStand(((CraftWorld)loc.getWorld()).getHandle());
			Location at = getInCircle(MathUtils.PI2 / 3 * i);
			stands[i].setPosition(at.x, at.y, at.z);
			stands[i].setNoGravity(true);
			stands[i].noclip = true;
			stands[i].setInvisible(true);
			((CraftArmorStand) stands[i].getBukkitEntity()).setHelmet(new ItemStack(Material.NETHER_STAR));
			stands[i].world.addEntity(stands[i], CreatureSpawnEvent.SpawnReason.CUSTOM);
		}
		
		runnable = new BukkitRunnable() {
			int animTime = 0;
			double angle = 0;
			@Override
			public void run() {
				if (animCnt > 0) {
					if (animTime < firstStage) {
						int st = animTime * 6 / firstStage;
						if (st % 2 == 0) {
							angle = (angle + MathUtils.PI2 * 2 / firstStage) % MathUtils.PI2;
							
							for (int i = 0; i < stands.length; i++) {
								Location at = getInCircle(angle + MathUtils.PI2 / 3 * i);
								stands[i].setPosition(at.x, at.y, at.z);
							}
							if (animTime % 30 == 15) {
								loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 1.5f, 1);
							}
						} else if (animTime * 6 % firstStage == 0) {
							loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 1.5f, 1);
						} else if (animTime % 30 == 15) {
							loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 1.5f, 1);
						}
					}
					if (animTime == firstStage) {
						PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
								new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
								CraftMagicNumbers.getBlock(loc.getBlock()), 1, 1);
						for (Player player : Bukkit.getOnlinePlayers()) {
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
						}
					}
					if (animTime == firstStage + 10) {
						loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 1.5f, 1);
					}
					if (firstStage + 10 <= animTime && animTime < animDuration - 85) {
						for (int i = 0; i < 5; i++) {
							Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 3, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
							at.y = Math.abs(at.y);
							loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.clone().add(0, 0.6, 0), 0,
									at.x, at.y, at.z);
						}
					}
					
					if (animTime == animDuration - 60) {
						PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
								new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
								CraftMagicNumbers.getBlock(loc.getBlock()), 1, 0);
						for (Player player : Bukkit.getOnlinePlayers()) {
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
						}
					}
					if (animDuration - 40 <= animTime && animTime <= animDuration) {
						if (animTime % 3 == 0) {
							loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1.5f, 1);
						}
						loc.getWorld().spawnParticle(Particle.LAVA, loc.clone().add(0, 0.6, 0), 1);
					}
					animTime++;
					if (animTime == animDuration) {
						loc.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1.5f, 1);
						for (int i = 0; i < 100; i++) {
							Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
							at.y = Math.abs(at.y);
							loc.getWorld().spawnParticle(Particle.FLAME, loc.clone(), 0, at.x, at.y, at.z);
						}
						animTime = 0;
						animCnt--;
					}
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
	
	public void unload() {
		loc.getBlock().setType(Material.AIR);
		for (EntityArmorStand stand : stands) {
			stand.die();
		}
		runnable.cancel();
	}
}