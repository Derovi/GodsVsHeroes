package by.dero.gvh.model;

import by.dero.gvh.FlyingText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class Stats {
    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> deaths = new HashMap<>();
    private final HashMap<Player, Double> damageDealt = new HashMap<>();
    private final HashMap<Player, Double> damageTaken = new HashMap<>();

    public void addKill(final LivingEntity target, final LivingEntity killer) {
        if (target instanceof Player) {
            if (killer instanceof Player && !target.equals(killer)) {
                kills.put((Player) killer, kills.getOrDefault(killer, 0) + 1);
            }
            deaths.put((Player) target, deaths.getOrDefault(target, 0) + 1);
        }
    }

    public void addDamage(final LivingEntity target, final LivingEntity killer, final double damage) {
        if (target instanceof Player) {
            if (killer instanceof Player && !target.equals(killer)) {
                damageDealt.put((Player) killer, damageDealt.getOrDefault(killer, 0.0) + damage);
            }
            damageTaken.put((Player) target, damageTaken.getOrDefault(target, 0.0) + damage);
        }
    }

    private Player getBestDamageDealt() {
        return damageDealt.entrySet().parallelStream().
                max(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey();
    }

    private Player getBestDamageTaken() {
        return damageTaken.entrySet().parallelStream().
                min(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey();
    }

    private Player getBestKills() {
        return kills.entrySet().parallelStream().
                max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
    }

    private Player getBestDeaths() {
        return deaths.entrySet().parallelStream().
                min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
    }

    final LinkedList<FlyingText> texts = new LinkedList<>();
    public void spawnStats(final Location loc) {
        for (final String message : new String[]{
                Lang.get("stats.label"),
                Lang.get("stats.bestKills").replace("%pl%", getBestKills().getName()),
                Lang.get("stats.bestDamageDealt").replace("%pl%", getBestDamageDealt().getName()),
                Lang.get("stats.bestDeaths").replace("%pl%", getBestDeaths().getName()),
                Lang.get("stats.bestDamageTaken").replace("%pl%", getBestDamageTaken().getName()),
        }) {
            final FlyingText text = new FlyingText(loc.clone(), message);
            Bukkit.getServer().broadcastMessage(message + " " + loc.clone().toVector());
            loc.subtract(0, 0.3, 0);
            texts.add(text);
        }
    }

    public void unload() {
        for (final FlyingText text : texts) {
            text.unload();
        }
    }
}
