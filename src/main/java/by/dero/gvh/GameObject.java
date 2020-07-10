package by.dero.gvh;


import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameObject {
	public LivingEntity entity;

	private int team = -1;
	private final HashMap<PotionEffectType, ArrayList<Pair<Long, Integer> > > effects = new HashMap<>();
	private Pair<Long, Integer> lastEffect = Pair.of(-1L, -1);

	public GameObject(LivingEntity entity) {
		this.entity = entity;
	}

	public long setBestEffect(PotionEffectType type) {
		long time = Minigame.getLastTicks();
		ArrayList<Pair<Long, Integer>> local = effects.get(type);
		local.removeIf((e) -> e.getKey() <= time);
		if (local.isEmpty()) {
			return -1;
		}
		long end = -1;
		int amp = -1;
		for (Pair<Long, Integer> ef : local) {
			if (amp < ef.getValue() || (amp == ef.getValue() && end < ef.getKey())) {
				amp = ef.getValue();
				end = ef.getKey();
			}
		}
		if (lastEffect.getKey() == end && lastEffect.getValue() == amp) {
			return -1;
		}
		lastEffect = Pair.of(end, amp);
		entity.addPotionEffect(new PotionEffect(type, Math.toIntExact(end - time), amp), true);
		return end;
	}

	public boolean updateEffect(PotionEffectType type) {
		long time = Minigame.getLastTicks();
		long end = setBestEffect(type);
		if (end == -1) {
			return false;
		}

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run () {
				if (!updateEffect(type)) {
					this.cancel();
				}
			}
		};
		runnable.runTaskLater(Plugin.getInstance(), end - time);
		Game.getInstance().getRunnables().add(runnable);
		return true;
	}

	public void removeEffect(PotionEffectType type) {
		if (effects.containsKey(type)) {
			effects.get(type).clear();
		}
	}

	public void addEffect(PotionEffect effect) {
		effects.putIfAbsent(effect.getType(), new ArrayList<>());

		long time = Minigame.getLastTicks();
		Pair<Long, Integer> value = Pair.of(time + effect.getDuration(), effect.getAmplifier());
		effects.get(effect.getType()).add(value);

		updateEffect(effect.getType());
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public LivingEntity getEntity() {
		return entity;
	}
}
