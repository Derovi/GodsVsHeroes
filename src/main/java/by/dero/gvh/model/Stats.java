package by.dero.gvh.model;

import by.dero.gvh.FlyingText;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Stats {
    private final long startTime;
    private final HashMap<String, Integer> kills = new HashMap<>();
    private final HashMap<String, Integer> deaths = new HashMap<>();
    private final HashMap<String, Integer> assists = new HashMap<>();
    private final HashMap<String, Double> damageDealt = new HashMap<>();
    private final HashMap<String, Double> damageTaken = new HashMap<>();
    private final HashMap<String, Integer> expGained = new HashMap<>();
    private final HashMap<String, Integer> capturePoints = new HashMap<>();
    
    public Stats() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void addKill(Player target, Player killer) {
        if (target != null) {
            if (killer != null && !target.equals(killer)) {
                kills.put(killer.getName(), kills.getOrDefault(killer.getName(), 0) + 1);
            }
            deaths.put(target.getName(), deaths.getOrDefault(target.getName(), 0) + 1);
        }
        if (Game.getInstance() instanceof DisplayInteractInterface) {
            ((DisplayInteractInterface) Game.getInstance()).updateDisplays();
        }
    }

    public void addKill(Player target, Player killer, Collection<Player> assists) {
        if (assists != null) {
            for (Player player : assists) {
                this.assists.put(player.getName(), this.assists.getOrDefault(player.getName(), 0) + 1);
            }
        }
        addKill(target, killer);
    }

    public void addDamage(Player target, Player killer, double damage) {
        if (target != null) {
            if (killer != null && !target.equals(killer)) {
                damageDealt.put(killer.getName(), damageDealt.getOrDefault(killer.getName(), 0.0) + damage);
            }
            damageTaken.put(target.getName(), damageTaken.getOrDefault(target.getName(), 0.0) + damage);
        }
    }

    public void addExp(String name, Integer value) {
        expGained.put(name, expGained.getOrDefault(name, 0) + value);

        if (Game.getInstance().getState().equals(Game.State.GAME) &&
                Game.getInstance() instanceof DisplayInteractInterface) {
            ((DisplayInteractInterface) Game.getInstance()).updateDisplays();
        }
    }
    
    public void addCapturePoints(String name, Integer points) {
        capturePoints.put(name, getCapturePoints(name) + points);
    }

    private String getBestDamageDealt() {
        String best = "";
        double val = -1;
        for (Map.Entry<String, Double> entry : damageDealt.entrySet()) {
            final String a = entry.getKey();
            final Double b = entry.getValue();
            if (val < b) {
                best = a;
                val = b;
            }
        }
        return best;
    }

    private String getBestDamageTaken() {
        final HashMap<String, GamePlayer> hm = Minigame.getInstance().getGame().getPlayers();
        String best = "";
        double val = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : damageTaken.entrySet()) {
            final String a = entry.getKey();
            final Double b = entry.getValue();
            if (!hm.containsValue(GameUtils.getPlayer(a))) {
                return a;
            }
            if (val > b) {
                best = a;
                val = b;
            }
        }
        return best;
    }

    private String getBestKills() {
        String best = "";
        int val = -1;
        for (Map.Entry<String, Integer> entry : kills.entrySet()) {
            final String a = entry.getKey();
            final int b = entry.getValue();
            if (val < b) {
                best = a;
                val = b;
            }
        }
        return best;
    }

    private String getBestDeaths() {
        String best = "";
        int val = Integer.MAX_VALUE;
        final HashMap<String, GamePlayer> hm = Minigame.getInstance().getGame().getPlayers();
        for (Map.Entry<String, Integer> entry : deaths.entrySet()) {
            final String a = entry.getKey();
            final int b = entry.getValue();
            if (!hm.containsValue(GameUtils.getPlayer(a))) {
                return a;
            }
            if (val > b) {
                best = a;
                val = b;
            }
        }
        return best;
    }

    final LinkedList<FlyingText> texts = new LinkedList<>();
    public void spawnStats(final Location loc) {
        for (final String message : new String[]{
                Lang.get("stats.label"),
                Lang.get("stats.bestKills").replace("%pl%", getBestKills()),
                Lang.get("stats.bestDamageDealt").replace("%pl%", getBestDamageDealt()),
                Lang.get("stats.bestDeaths").replace("%pl%", getBestDeaths()),
                Lang.get("stats.bestDamageTaken").replace("%pl%", getBestDamageTaken()),
        }) {
            final FlyingText text = new FlyingText(loc.clone(), message);
            loc.subtract(0, 0.3, 0);
            texts.add(text);
        }
    }

    public void unload() {
        for (final FlyingText text : texts) {
            text.unload();
        }
    }

    public long getDuration() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    public Integer getKills(String name) {
        return kills.getOrDefault(name, 0);
    }

    public Integer getDeaths(String name) {
        return deaths.getOrDefault(name, 0);
    }
    
    public Integer getAssists(String name) {
        return assists.getOrDefault(name, 0);
    }

    public Integer getExpGained(String name) {
        return expGained.getOrDefault(name, 0);
    }
    
    public Integer getCapturePoints(String name) {
        return capturePoints.getOrDefault(name, 0);
    }
}
