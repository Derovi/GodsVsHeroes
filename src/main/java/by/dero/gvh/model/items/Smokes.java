package by.dero.gvh.model.items;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.SmokesInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.SpawnUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class Smokes extends Item implements InfiniteReplenishInterface, PlayerInteractInterface, ProjectileHitInterface {
	private final double radius;
	private final int duration;

	public Smokes (String name, int level, Player owner) {
		super(name, level, owner);

		SmokesInfo info = (SmokesInfo) getInfo();
		radius = info.getRadius();
		duration = info.getDuration();
	}

	@Override
	public void onPlayerInteract (PlayerInteractEvent event) {
		final Projectile proj = SpawnUtils.spawnProjectile(owner.getEyeLocation(),
				1.2, EntityType.SNOWBALL, owner);
		owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
		summonedEntityIds.add(proj.getUniqueId());
	}

	private static final HashSet<Pair<Location, Integer>> poses = new HashSet<>();

	private void smoke() {
		for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
			Player player = gp.getPlayer();
			boolean inside = false;
			for (Pair<Location, Integer> pose : poses) {
				if (gp.getTeam() != pose.getValue() && pose.getKey().distance(player.getLocation()) < radius) {
					if (!player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 4), true);
					}
					inside = true;
					break;
				}
			}
			if (!inside) {
				player.removePotionEffect(PotionEffectType.BLINDNESS);
			}
		}
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		Location loc = event.getEntity().getLocation();
		int team = GameUtils.getPlayer(owner.getName()).getTeam();
		poses.add(Pair.of(loc, team));
		BukkitRunnable effects = new BukkitRunnable() {
			int time = 0;
			@Override
			public void run () {
				smoke();

				if (time % 60 == 0) {
					loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1.07f, 1);
				}
				time += 2;
				if (time > duration) {
					poses.remove(Pair.of(loc, team));
					smoke();
					this.cancel();
				}
			}
		};
		effects.runTaskTimer(Plugin.getInstance(), 0, 2);
		Game.getInstance().getRunnables().add(effects);
		BukkitRunnable smoke = new BukkitRunnable() {
			int time = 0;

			@Override
			public void run () {
				for (int i = 0; i < 3; i++) {
					Location at = MathUtils.randomCylinder(loc, radius, -2);
					at.getWorld().spawnParticle(Particle.SMOKE_LARGE, at, 0, 0, 0.1, 0);
				}
				time++;
				if (time > duration) {
					this.cancel();
				}
			}
		};
		smoke.runTaskTimer(Plugin.getInstance(), 0, 1);
		Game.getInstance().getRunnables().add(smoke);
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
