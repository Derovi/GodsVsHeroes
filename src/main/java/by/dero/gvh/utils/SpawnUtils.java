package by.dero.gvh.utils;

import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftZombie;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpawnUtils {
	public static void spawnLightning(Location at, double damage, double sound, GamePlayer owner) {
		EntityLightning entity = new EntityLightning(((CraftWorld)at.getWorld()).world,
				at.getX(), at.getY(), at.getZ(), false);
		
		at.getWorld().playSound(at, Sound.ENTITY_LIGHTNING_THUNDER, (float) sound, 1);
		for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
			Player player = gp.getPlayer();
			if (player.getLocation().distance(at) <= 2 && owner.getTeam() != gp.getTeam()) {
				GameUtils.damage(damage, player, owner.getPlayer());
			}
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(entity));
		}
		for (GameMob gm : Game.getInstance().getMobs().values()) {
			if (gm.getEntity().isDead()) {
				continue;
			}
			if (gm.getEntity().getLocation().distance(at) <= 2 && owner.getTeam() != gm.getTeam()) {
				GameUtils.damage(damage, gm.getEntity(), owner.getPlayer());
			}
		}
	}
	
	public static Entity spawnEntity(final Location loc, final EntityType type) {
		CraftWorld wrld = ((CraftWorld) loc.getWorld());
		net.minecraft.server.v1_12_R1.Entity entity = wrld.createEntity(loc, type.getEntityClass());
		entity.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
		wrld.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
		return entity.getBukkitEntity();
	}
	
	public static GameMob spawnTeamEntity(Location loc, EntityType type, GamePlayer gp) {
		loc = loc.getWorld().getHighestBlockAt(loc).getLocation().add(0, 1, 0);
		LivingEntity entity = (LivingEntity) spawnEntity(loc, type);
		GameMob gm = new GameMob(entity, gp.getTeam(), gp.getPlayer());
		gm.updateName();
		return gm;
	}
	
	public static Projectile spawnProjectile(final Location at, final double speed,
	                                         final EntityType type, final Player player) {
		final Vector dir = at.getDirection().clone();
		
		final Location loc = at.clone().add(dir.clone().multiply(1.8));
		Projectile obj = (Projectile) spawnEntity(loc, type);
		obj.setVelocity(dir.multiply(speed));
		obj.setShooter(player);
		
		return obj;
	}
	
	public static Projectile spawnSplashPotion(Location at, double speed, PotionType type, Player player) {
		Potion instance = new Potion(type, 1, true);
		
		EntityPlayer pl = ((CraftPlayer) player).getHandle();
		EntityPotion potion = new EntityPotion(pl.world, pl, CraftItemStack.asCraftCopy(instance.toItemStack(1)).handle);
		potion.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(),""));
		Vector dir = at.getDirection();
		Location loc = at.clone().add(dir.clone().multiply(2));
		potion.locX = loc.x;
		potion.locY = loc.y;
		potion.locZ = loc.z;
		potion.motX = dir.x * speed;
		potion.motY = dir.y * speed;
		potion.motZ = dir.z * speed;
		
		potion.world.addEntity(potion, CreatureSpawnEvent.SpawnReason.CUSTOM);
		
		return (Projectile) potion.getBukkitEntity();
	}
	
	public static GameMob spawnAIZombie(Location loc, int hp, int attackDamage,
	                                    int duration, boolean isBaby, GamePlayer ownerGP) {
		GameMob gm = spawnTeamEntity(loc, EntityType.ZOMBIE, ownerGP);
		CraftZombie entity = (CraftZombie) gm.getEntity();
		EntityZombie zombie = entity.getHandle();
		//armor
		zombie.getAttributeInstance(GenericAttributes.i).setValue(20);
		zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(attackDamage);
		zombie.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100);
		zombie.getAttributeInstance(GenericAttributes.maxHealth).setValue(hp);
		zombie.setHealth(hp);
		zombie.targetSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
		zombie.goalSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
		
		zombie.fireProof = true;
		zombie.goalSelector.a(0, new PathfinderGoalFloat(zombie));
		zombie.goalSelector.a(2, new PathfinderGoalZombieAttack(zombie, 1.0D, false));
		zombie.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(zombie, 1.0D));
		zombie.goalSelector.a(7, new PathfinderGoalRandomStrollLand(zombie, 1.0D));
		zombie.goalSelector.a(8, new PathfinderGoalLookAtPlayer(zombie, EntityHuman.class, 8.0F));
		zombie.goalSelector.a(8, new PathfinderGoalRandomLookaround(zombie));
		zombie.targetSelector.a(0, new PathfinderAttackEnemies<>(
				zombie, EntityLiving.class, 100, true, false, GameUtils.getTargetPredicate(ownerGP.getTeam())));
		
		zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6);
		zombie.setBaby(isBaby);
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				if (!entity.isDead()) {
					entity.remove();
				}
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), duration);
		Game.getInstance().getRunnables().add(runnable);
		gm.updateName();
		return gm;
	}
	
	public static GameMob spawnAISkeleton(Location loc, int hp, int attackDamage,
	                                      boolean isMelee, int duration, GamePlayer ownerGP) {
		final GameMob gm = isMelee ? spawnTeamEntity(loc, EntityType.WITHER_SKELETON, ownerGP) :
				spawnTeamEntity(loc, EntityType.SKELETON, ownerGP);
		CraftSkeleton skeleton = (CraftSkeleton) gm.getEntity();
		EntitySkeletonAbstract handle = skeleton.getHandle();
		handle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(attackDamage);
		//armor
		handle.getAttributeInstance(GenericAttributes.i).setValue(20);
		handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100);
		handle.getAttributeInstance(GenericAttributes.maxHealth).setValue(hp);
		handle.setHealth(hp);
		handle.fireProof = true;
		
		handle.goalSelector = new PathfinderGoalSelector(handle.world.methodProfiler);
		handle.targetSelector = new PathfinderGoalSelector(handle.world.methodProfiler);
		handle.targetSelector.a(0, new PathfinderAttackEnemies<>(
				handle, EntityLiving.class, 100, true, false, GameUtils.getTargetPredicate(ownerGP.getTeam())));
		
		handle.goalSelector.a(1, new PathfinderGoalFloat(handle));
		handle.goalSelector.a(handle.c);
		handle.goalSelector.a(handle.b);
		handle.goalSelector.a(4, isMelee ? handle.c : handle.b);
		handle.goalSelector.a(5, new PathfinderGoalRandomStrollLand(handle, 1.0D));
		handle.goalSelector.a(6, new PathfinderGoalLookAtPlayer(handle, EntityHuman.class, 8.0F));
		handle.goalSelector.a(6, new PathfinderGoalRandomLookaround(handle));
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				handle.die();
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), duration);
		Game.getInstance().getRunnables().add(runnable);
		gm.updateName();
		return gm;
	}
}
