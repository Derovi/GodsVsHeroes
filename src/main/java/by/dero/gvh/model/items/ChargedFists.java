package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChargedFistsInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChargedFists extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
	private final double forceDistance;
	private final int damage;
	private final int duration;
	private final double force;
	private final double range = 6;
	
	public ChargedFists(String name, int level, Player owner) {
		super(name, level, owner);
		
		ChargedFistsInfo info = (ChargedFistsInfo) getInfo();
		damage = info.getDamage();
		duration = info.getDuration();
		force = info.getForce();
		forceDistance = info.getForceDistance();
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		DoubleFist item = (DoubleFist) ownerGP.getItems().get("doublefist");
		int was = item.getDamage();
		double wasRange = item.getRange();
		
		Drawings.drawCircle(owner.getLocation().add(0, 2, 0), 1.5, Particle.DRAGON_BREATH, 8);
		
		item.setRange(range);
		item.setDamage(damage);
		item.setRed(true);
		item.setOnHit(() -> {
			if (GameUtils.getTargetEntity(owner, forceDistance + 0.5, (e) -> GameUtils.isEnemy(e, getTeam())) != null) {
				owner.setVelocity(owner.getLocation().getDirection().multiply(force));
			}
		});
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				item.setRed(false);
				item.setDamage(was);
				item.setRange(wasRange);
				item.setOnHit(null);
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), duration);
		Game.getInstance().getRunnables().add(runnable);
	}
}
