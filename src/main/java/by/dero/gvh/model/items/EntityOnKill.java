package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.DeathMatch;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerKillInterface;
import by.dero.gvh.model.itemsinfo.EntityOnKillInfo;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityMonster;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftMonster;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityOnKill extends Item implements PlayerKillInterface {
	private final EntityType entityType;

	public EntityOnKill (String name, int level, Player owner) {
		super(name, level, owner);

		EntityOnKillInfo info = (EntityOnKillInfo) getInfo();
		entityType = info.getType();
	}

	@Override
	public void onPlayerKill (Player target) {
		CraftMonster entity = (CraftMonster) GameUtils.spawnTeamEntity(target.getLocation(),
				entityType, GameUtils.getPlayer(owner.getName()));

		GameUtils.addTeamAi(entity, getTeam());
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
