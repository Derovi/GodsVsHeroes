package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.minigame.deathmatch.DeathMatch;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerKillInterface;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityOnKill extends Item implements PlayerKillInterface {
	public EntityOnKill (String name, int level, Player owner) {
		super(name, level, owner);
	}

	@Override
	public void onPlayerKill (Player target) {
		int resp = DeathMatch.getInstance().getInfo().getRespawnTime();
		GameMob mob = SpawnUtils.spawnAIZombie(target.getLocation(), 30, 4, resp, false, ownerGP);
		mob.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, resp, 1), true);
	}
}
