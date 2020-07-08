package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.ThorBowInfo;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityLightning;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityWeather;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ThorBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
	private final double damage;

	public ThorBow (String name, int level, Player owner) {
		super(name, level, owner);
		ThorBowInfo info = (ThorBowInfo) getInfo();
		damage = info.getDamage();
	}

	@Override
	public void onPlayerShootBow (EntityShootBowEvent event) {
		if (!cooldown.isReady()) {
			event.getProjectile().remove();
			event.setCancelled(true);
			return;
		}
		cooldown.reload();
		Arrow arrow = (Arrow) event.getProjectile();
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				arrow.getWorld().spawnParticle(Particle.LAVA, arrow.getLocation(), 0, 0, 0, 0);
				if (arrow.isDead()) {
					this.cancel();
				}
			}
		};
		runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
	}

	@Override
	public void onProjectileHit (ProjectileHitEvent event) {
		Location at = event.getEntity().getLocation();
//		CustomLightning lightning = new CustomLightning(((CraftWorld)at.getWorld()).getHandle(), at.x, at.y, at.z, false, false);
//		lightning.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
//		GameUtils.setLastUsedLightning(owner);
//		owner.getWorld().strikeLightning(at);
//		lightning.getWorld().addEntity(lightning, CreatureSpawnEvent.SpawnReason.CUSTOM);
		EntityLightning entity = new EntityLightning(((CraftWorld)at.getWorld()).world,
				at.getX(), at.getY(), at.getZ(), false);
		for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
			Player player = gp.getPlayer();
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(entity));

			if (player.getLocation().distance(at) <= 2 && getTeam() != gp.getTeam()) {
				GameUtils.damage(damage, player, owner);
			}
		}
		for (GameMob gm : Game.getInstance().getMobs().values()) {
			if (gm.getEntity().getLocation().distance(at) <= 2 && getTeam() != gm.getTeam()) {
				GameUtils.damage(damage, gm.getEntity(), owner);
			}
		}
	}

	@Override
	public void onProjectileHitEnemy (ProjectileHitEvent event) {

	}
}
