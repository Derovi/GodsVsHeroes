package by.dero.gvh.model.items;

import by.dero.gvh.Cooldown;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleHandInteractInterface;
import by.dero.gvh.model.interfaces.DoubleHanded;
import by.dero.gvh.model.itemsinfo.DoubleFistInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoubleFist extends Item implements DoubleHanded, DoubleHandInteractInterface {
	private final Cooldown cooldownOffhand;
	private final int damage;
	private long lastUsed;
	public DoubleFist(String name, int level, Player owner) {
		super(name, level, owner);
		cooldownOffhand = new Cooldown(this.cooldown.getDuration());
		
		DoubleFistInfo info = (DoubleFistInfo) getInfo();
		damage = info.getDamage();
	}
	
	@Override
	public void interactMainHand(PlayerInteractEvent event) {
		if (!cooldown.isReady() || System.currentTimeMillis() - lastUsed < 100) {
			return;
		}
		cooldown.reload();
		lastUsed = System.currentTimeMillis();
		LivingEntity ent = GameUtils.getTargetEntity(owner, 3.3, (e) -> GameUtils.isEnemy(e, getTeam()));
		if (ent != null) {
			GameUtils.damage(damage, ent, owner);
		}
	}
	
	@Override
	public void interactOffHand(PlayerInteractEvent event) {
		if (!cooldownOffhand.isReady() || System.currentTimeMillis() - lastUsed < 100) {
			return;
		}
		cooldownOffhand.reload();
		lastUsed = System.currentTimeMillis();
		
		LivingEntity ent = GameUtils.getTargetEntity(owner, 3.3, (e) -> GameUtils.isEnemy(e, getTeam()));
		if (ent != null) {
			GameUtils.damage(damage, ent, owner);
		}
	}
}
