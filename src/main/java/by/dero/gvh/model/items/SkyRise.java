package by.dero.gvh.model.items;

import by.dero.gvh.GameObject;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.SkyRiseInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SafeRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class SkyRise extends Item implements DoubleSpaceInterface {
	private final double radius;
	private final double damage;

	public SkyRise (String name, int level, Player owner) {
		super(name, level, owner);

		SkyRiseInfo info = (SkyRiseInfo) getInfo();
		radius = info.getRadius();
		damage = info.getDamage();
	}

	@Override
	public void onDoubleSpace () {
		if (owner.getInventory().getItem(0).getType().equals(Material.STAINED_GLASS_PANE)) {
			if (!ownerGP.isActionBarBlocked()) {
				ownerGP.setActionBarBlocked(true);
				owner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Lang.get("game.cantUse")));
				new BukkitRunnable() {
					@Override
					public void run() {
						ownerGP.setActionBarBlocked(false);
					}
				}.runTaskLater(Plugin.getInstance(), 30);
			}
			return;
		}
		if (!cooldown.isReady()) {
			GameUtils.doubleSpaceCooldownMessage(this);
			return;
		}
		cooldown.reload();
		ArmorStand stand = (ArmorStand) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.ARMOR_STAND);
		stand.setVelocity(new Vector(0, 0.49, 0));
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				owner.setVelocity(new Vector(0, 1.4, 0));
				owner.addPassenger(stand);
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), 10);
		Game.getInstance().getRunnables().add(runnable);
		Mjolnir mjolnir = (Mjolnir) ownerGP.getItems().get("mjolnir");
		owner.getInventory().setItem(0, Item.getPane(owner.getInventory().getItem(0).getI18NDisplayName()));
		SafeRunnable runnable1 = new SafeRunnable() {
			int ticks = 0;
			@Override
			public void run() {
				if (owner.getLocation().y * 2 == (int) owner.getLocation().y * 2 || ticks > 80) {
					this.cancel();
					owner.getInventory().setItem(0, mjolnir.getItemStack());
					owner.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, owner.getLocation(), 1);
					owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					stand.remove();
					owner.getInventory().setItem(0, mjolnir.getItemStack());
					for (GameObject go : GameUtils.getGameObjects()) {
						LivingEntity ent = go.getEntity();
						if (ent.getLocation().distance(owner.getLocation()) < radius && go.getTeam() != getTeam()) {
							GameUtils.damage(damage, ent, owner, false);
							ent.setVelocity(ent.getLocation().subtract(owner.getLocation()).toVector().normalize().multiply(1.2));
							Location at = ent.getLocation();
							at.getWorld().playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
							at.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, at, 1);
							break;
						}
					}
				}
				ticks += 2;
			}
		};
		runnable1.runTaskTimer(Plugin.getInstance(), 11, 2);
		Game.getInstance().getRunnables().add(runnable1);
		stand.setItemInHand(mjolnir.getItemStack());
		stand.setVisible(false);
		stand.setRightArmPose(new EulerAngle(Math.PI * 1.5, 0, 0));
		for (GameObject go : GameUtils.getGameObjects()) {
			LivingEntity ent = go.getEntity();
			Location loc = new Location(owner.getWorld(),
					ent.getLocation().getX(), owner.getLocation().getY(), ent.getLocation().getZ());
			if (go.getTeam() != getTeam() && loc.distance(owner.getLocation()) < radius) {
				ent.setVelocity(owner.getLocation().subtract(loc).toVector().normalize().multiply(1.2));
			}
		}
	}
}
