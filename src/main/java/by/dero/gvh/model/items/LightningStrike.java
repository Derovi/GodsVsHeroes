package by.dero.gvh.model.items;

import by.dero.gvh.GameObject;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.LightningStrikeInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SpawnUtils;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

public class LightningStrike extends Item implements PlayerInteractInterface {
	private final double damage;
	private final double radius;
	private final double lightningDamage;
	private final Material material;
	
	public LightningStrike(String name, int level, Player owner) {
		super(name, level, owner);
		
		LightningStrikeInfo info = (LightningStrikeInfo) getInfo();
		damage = info.getDamage();
		radius = info.getRadius();
		lightningDamage = info.getLightningDamage();
		material = info.getMaterial();
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!cooldown.isReady()) {
			return;
		}
		cooldown.reload();
		owner.setCooldown(material, (int) cooldown.getDuration());
		BlockIterator it = new BlockIterator(owner.getEyeLocation(), 0, 100);
		Location at = null;
		while (it.hasNext()) {
			Block block = it.next();
			if (block.getType().equals(Material.AIR)) {
				continue;
			}
			at = block.getLocation().clone();
			break;
		}
		if (at == null) {
			return;
		}
		SpawnUtils.spawnLightning(at, lightningDamage, 2, ownerGP);
		at.getWorld().playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
		at.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 1);
		for (GameObject obj : GameUtils.getGameObjects()) {
			LivingEntity ent = obj.getEntity();
			if (ent.getLocation().getY() - 4 - radius > at.getY()) {
				continue;
			}
			Location to = ent.getEyeLocation();
			double dst = (to.x - at.x) * (to.x - at.x) + (to.z - at.z) * (to.z - at.z);
			if (obj.getTeam() != getTeam() && dst < radius * radius) {
				GameUtils.damage(damage, ent, owner);
				Location fr = at.clone().add(0, Math.random() * 4, 0);
				Drawings.drawLine(ent.getEyeLocation(), fr, Particle.END_ROD);
				Stun.stunEntity(ent, 20);
				to.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, to, 1);
				to.getWorld().playSound(to, Sound.ENTITY_IRONGOLEM_DEATH, 1.5f, 1);
			}
		}
	}
}
