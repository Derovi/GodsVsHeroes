package by.dero.gvh.lobby.monuments;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.InterfaceUtils;
import by.dero.gvh.utils.MathUtils;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.LinkedList;

public class DonatePackChest {
	private static class Anim {
		@Getter private final int type;
		@Getter private final Player player;
		@Getter private final ItemStack carry;
		@Getter private final Runnable[] runnable;
		public Anim(int type, Player player, ItemStack carry, Runnable... runnable) {
			this.type = type;
			this.player = player;
			this.carry = carry;
			if (runnable == null || runnable.length == 0) {
				this.runnable = null;
			} else {
				this.runnable = runnable;
			}
		}
	}
	
	private final LinkedList<Anim> anims = new LinkedList<>();
	@Getter
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
		
		Location stText = loc.clone().add(0, 0.4, -0.2);
		FlyingText text =  new FlyingText(stText, Lang.get("lobby.donate"));
		runnable = new BukkitRunnable() {
			final Material[] drops = {Material.EMERALD, Material.EMERALD_BLOCK, Material.SLIME_BLOCK};
			int animTime = 0;
			double angle = 0;
			float fr = 1.1f;
			EntityArmorStand carryStand;
			final LinkedList<EntityItem> list = new LinkedList<>();
			@Override
			public void run() {
				if (anims.size() > 0) {
					if (animTime == 0) {
						text.setText(Lang.get("interfaces.openingTitle").
								replace("%pl%", anims.getFirst().getPlayer().getName()));
						if (anims.getFirst().getRunnable() != null && anims.getFirst().getRunnable()[0] != null) {
							anims.getFirst().getRunnable()[0].run();
						}
					}
					if (anims.getFirst().getType() == 4) {
						if (animTime == 0) {
							PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
									new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
									CraftMagicNumbers.getBlock(loc.getBlock()), 1, 1);
							for (Player player : Bukkit.getOnlinePlayers()) {
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
							}
						}
						if (animTime == 10) {
							loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 2f, 1);
						}
						if (20 <= animTime && animTime < 80) {
							int ticks = animTime - 20;
							Location iloc = loc.clone().add(0, 0.6, -0.2);
							if (ticks % 5 == 0) {
								ItemStack item = new ItemStack(drops[ticks / 5 % drops.length]);
								InterfaceUtils.changeName(item, String.valueOf(animTime));
								EntityItem entity = new EntityItem(((CraftWorld) iloc.getWorld()).getHandle(),
										iloc.getX(), iloc.getY(), iloc.getZ(), CraftItemStack.asNMSCopy(item));
								entity.pickupDelay = 10000000;
								entity.motY = 0.2;
								double angle = Math.random() * MathUtils.PI2;
								entity.motX = MathUtils.cos(angle) * 0.2;
								entity.motZ = MathUtils.sin(angle) * 0.2;
								
								entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
								list.add(entity);
							}
							if (ticks % 20 == 0) {
								fr += 0.15;
							}
							if (ticks % 20 < 10 && ticks % 2 == 0) {
								iloc.getWorld().playSound(iloc, Sound.BLOCK_NOTE_HARP, 2, fr + (float) ticks % 20 / 10 / 2);
							}
						
						}
						
						if (10 <= animTime && animTime < 20) {
							for (int i = 0; i < stands.length; i++) {
								Location at = getInCircle(MathUtils.PI2 * (animTime - 9) / 6 / 10 + MathUtils.PI2 / 3 * i);
								stands[i].setPosition(at.x, at.y, at.z);
								text.getArmorStand().getHandle().locY = stText.y + 1.5 / 10 * (animTime - 9);
							}
						}
						if (animTime == 20) {
							loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
						}
						if (10 <= animTime && animTime < 45) {
							for (int i = 0; i < 5; i++) {
								Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 3, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
								at.y = Math.abs(at.y);
								loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.clone().add(0, 0.6, 0), 0,
										at.x, at.y, at.z);
							}
						}
						
						if (animTime == 80) {
							loc.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 2, 1);
							for (int i = 0; i < 100; i++) {
								Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
								at.y = Math.abs(at.y);
								loc.getWorld().spawnParticle(Particle.FLAME, loc.clone(), 0, at.x, at.y, at.z);
							}
							carryStand = new EntityArmorStand(stands[0].getWorld(), loc.x, loc.y-1.4, loc.z-0.2);
							GameUtils.setInvisibleFlags((ArmorStand) carryStand.getBukkitEntity());
							if (anims.getFirst().getCarry() != null) {
								((CraftArmorStand) carryStand.getBukkitEntity()).setHelmet(anims.getFirst().getCarry());
							}
							carryStand.world.addEntity(carryStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
						}
						
						if (80 <= animTime && animTime <= 160) {
							carryStand.setHeadPose(new Vector3f(0, carryStand.headPose.y + 360.0f / 20, 0));
						}
						
						if (animTime == 160) {
							carryStand.die();
							PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
									new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
									CraftMagicNumbers.getBlock(loc.getBlock()), 1, 0);
							for (Player player : Bukkit.getOnlinePlayers()) {
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
							}
						}
						
						if (165 <= animTime && animTime < 175) {
							for (int i = 0; i < stands.length; i++) {
								Location at = getInCircle(MathUtils.PI2 * (174 - animTime) / 6 / 10 + MathUtils.PI2 / 3 * i);
								stands[i].setPosition(at.x, at.y, at.z);
								text.getArmorStand().getHandle().locY = stText.y + 1.5 / 10 * (174 - animTime);
							}
						}
						
						animTime++;
						if (animTime == 175) {
							for (EntityItem ent : list) {
								ent.die();
							}
							list.clear();
							loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
							text.setText(Lang.get("lobby.donate"));
							animTime = 0;
							anims.removeFirst();
						}
					} else if (anims.getFirst().getType() == 3) {
						if (animTime == 0) {
							PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
									new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
									CraftMagicNumbers.getBlock(loc.getBlock()), 1, 1);
							for (Player player : Bukkit.getOnlinePlayers()) {
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
							}
						}
						if (animTime == 10) {
							loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 2, 1);
						}
						if (10 <= animTime && animTime < 20) {
							for (int i = 0; i < stands.length; i++) {
								Location at = getInCircle(MathUtils.PI2 * (animTime - 9) / 6 / 10 + MathUtils.PI2 / 3 * i);
								stands[i].setPosition(at.x, at.y, at.z);
								text.getArmorStand().getHandle().locY = stText.y + 1.5 / 10 * (animTime - 9);
							}
						}
						if (animTime == 20) {
							loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
						}
						if (10 <= animTime && animTime < 45) {
							for (int i = 0; i < 5; i++) {
								Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 3, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
								at.y = Math.abs(at.y);
								loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.clone().add(0, 0.6, 0), 0,
										at.x, at.y, at.z);
							}
						}
						
						if (animTime == 80) {
							loc.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 2, 1);
							for (int i = 0; i < 100; i++) {
								Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
								at.y = Math.abs(at.y);
								loc.getWorld().spawnParticle(Particle.FLAME, loc.clone(), 0, at.x, at.y, at.z);
							}
							carryStand = new EntityArmorStand(stands[0].getWorld(), loc.x, loc.y-1.4, loc.z-0.2);
							GameUtils.setInvisibleFlags((ArmorStand) carryStand.getBukkitEntity());
							if (anims.getFirst().getCarry() != null) {
								((CraftArmorStand) carryStand.getBukkitEntity()).setHelmet(anims.getFirst().getCarry());
							}
							carryStand.world.addEntity(carryStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
						}
						
						if (80 <= animTime && animTime <= 160) {
							carryStand.setHeadPose(new Vector3f(0, carryStand.headPose.y + 360.0f / 20, 0));
						}
						
						if (animTime == 160) {
							carryStand.die();
							PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
									new BlockPosition(loc.x - 0.5, loc.y - 0.4375, loc.z - 0.7),
									CraftMagicNumbers.getBlock(loc.getBlock()), 1, 0);
							for (Player player : Bukkit.getOnlinePlayers()) {
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
							}
						}
						
						if (165 <= animTime && animTime < 175) {
							for (int i = 0; i < stands.length; i++) {
								Location at = getInCircle(MathUtils.PI2 * (174 - animTime) / 6 / 10 + MathUtils.PI2 / 3 * i);
								stands[i].setPosition(at.x, at.y, at.z);
								text.getArmorStand().getHandle().locY = stText.y + 1.5 / 10 * (174 - animTime);
							}
						}
						
						animTime++;
						if (animTime == 175) {
							loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
							text.setText(Lang.get("lobby.donate"));
							animTime = 0;
							anims.removeFirst();
						}
					} else {
						if (animTime < firstStage) {
							int st = animTime * 6 / firstStage;
							if (st % 2 == 0) {
								angle = (angle + MathUtils.PI2 * 2 / firstStage) % MathUtils.PI2;
								
								for (int i = 0; i < stands.length; i++) {
									Location at = getInCircle(angle + MathUtils.PI2 / 3 * i);
									stands[i].setPosition(at.x, at.y, at.z);
								}
								if (animTime % 30 == 15) {
									loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
								}
							} else if (animTime * 6 % firstStage == 0) {
								loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 2, 1);
							} else if (animTime % 30 == 15) {
								loc.getWorld().playSound(loc, Sound.BLOCK_COMPARATOR_CLICK, 2, 1);
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
							loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 2, 1);
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
								loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
							}
							loc.getWorld().spawnParticle(Particle.LAVA, loc.clone().add(0, 0.6, 0), 1);
						}
						animTime++;
						if (animTime == animDuration) {
							loc.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 2, 1);
							if (anims.getFirst().getType() == 0) {
								for (int i = 0; i < 100; i++) {
									Vector at = MathUtils.getInCphere(MathUtils.ZEROVECTOR, 1, MathUtils.PI2 * Math.random(), MathUtils.PI2 * Math.random());
									at.y = Math.abs(at.y);
									loc.getWorld().spawnParticle(Particle.FLAME, loc.clone(), 0, at.x, at.y, at.z);
								}
							} else {
								for (int i = 0; i < 20; i++) {
									new BukkitRunnable() {
										final Location target = Lobby.getInstance().getMonumentManager().getBoosters().
												get(anims.getFirst().getType() - 1).getLocation();
										@Override
										public void run() {
											for (int j = 0; j < 3; j++) {
												Vector at = MathUtils.getInCphere(target.toVector(), 0.35, MathUtils.PI2 * Math.random(),
														MathUtils.PI2 * Math.random()).subtract(loc.toVector()).multiply(0.075);
												
												loc.getWorld().spawnParticle(Particle.FLAME, loc, 0,
														at.x, at.y, at.z);
											}
										}
									}.runTaskLater(Plugin.getInstance(), i);
								}
							}
							text.setText(Lang.get("lobby.donate"));
							animTime = 0;
							anims.removeFirst();
						}
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
	
	public void addAnim(int type, Player player, ItemStack carry, Runnable... runnable) {
		anims.add(new Anim(type, player, carry, runnable));
	}
}
