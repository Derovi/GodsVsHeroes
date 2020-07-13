package by.dero.gvh.model.items;

import by.dero.gvh.GameObject;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.SkyRiseInfo;
import by.dero.gvh.nmcapi.PlayerUtils;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SkyRise extends Item implements DoubleSpaceInterface {
	private final double radius;
	private final double damage;
	private final int explosions;

	public SkyRise (String name, int level, Player owner) {
		super(name, level, owner);

		SkyRiseInfo info = (SkyRiseInfo) getInfo();
		radius = info.getRadius();
		damage = info.getDamage();
		explosions = info.getExplosions();
	}

	@Override
	public void onDoubleSpace () {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();

		PlayerUtils.jumpUp(owner, 23);
		for (GameObject go : GameUtils.getGameObjects()) {
			LivingEntity ent = go.getEntity();
			Location loc = new Location(owner.getWorld(),
					ent.getLocation().getX(), owner.getLocation().getY(), ent.getLocation().getZ());
			if (go.getTeam() != getTeam() && loc.distance(owner.getLocation()) < radius) {
				ent.setVelocity(owner.getLocation().subtract(loc).toVector().normalize().multiply(1.2));
			}
		}
		BukkitRunnable activate = new BukkitRunnable() {
			@Override
			public void run () {
				ArrayList<Location> list = new ArrayList<>(explosions);
				for (int i = 0; i < explosions; i++) {
					Location at = MathUtils.randomCylinder(owner.getLocation().subtract(0, 1, 0), radius, 0);
					list.add(at);
					at.getWorld().playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					at.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 1);
				}
				for (GameObject go : GameUtils.getGameObjects()) {
					LivingEntity ent = go.getEntity();
					for (Location loc : list) {
						if (go.getTeam() != getTeam() && loc.distance(owner.getLocation()) < 4) {
							GameUtils.damage(damage, ent, owner, false);
							ent.setVelocity(ent.getLocation().subtract(loc).toVector().normalize().multiply(0.5));
							break;
						}
					}
				}
			}
		};
		activate.runTaskLater(Plugin.getInstance(), 28);
		Game.getInstance().getRunnables().add(activate);
	}
}
