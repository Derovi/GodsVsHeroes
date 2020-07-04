package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.DeathMatch;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerKillInterface;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.PathfinderAttackEnemies;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityOnKill extends Item implements PlayerKillInterface {
	public EntityOnKill (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerKill (Player target) {
		CraftZombie entity = (CraftZombie) GameUtils.spawnTeamEntity(target.getLocation(),
				EntityType.ZOMBIE, GameUtils.getPlayer(owner.getName()));
		
		EntityZombie zombie = entity.getHandle();
		zombie.targetSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);
		zombie.goalSelector = new PathfinderGoalSelector(zombie.world.methodProfiler);

		zombie.goalSelector.a(0, new PathfinderGoalFloat(zombie));
		zombie.goalSelector.a(2, new PathfinderGoalZombieAttack(zombie, 1.0D, false));
		zombie.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(zombie, 1.0D));
		zombie.goalSelector.a(7, new PathfinderGoalRandomStrollLand(zombie, 1.0D));
		zombie.goalSelector.a(8, new PathfinderGoalLookAtPlayer(zombie, EntityHuman.class, 8.0F));
		zombie.goalSelector.a(8, new PathfinderGoalRandomLookaround(zombie));
		zombie.targetSelector.a(0, new PathfinderAttackEnemies<>(
				zombie, EntityLiving.class, 50, true, false, GameUtils.getTargetPredicate(getTeam())));

		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				if (!entity.isDead()) {
					entity.remove();
				}
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), DeathMatch.getInstance().getInfo().getRespawnTime());
		Game.getInstance().getRunnables().add(runnable);
	}
}
