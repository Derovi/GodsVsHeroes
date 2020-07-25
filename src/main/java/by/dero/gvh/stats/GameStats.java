package by.dero.gvh.stats;

import by.dero.gvh.FlyingText;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class GameStats {
    @SerializedName("_id")
    @Getter private int id;

    @Getter private final HashMap<String, GamePlayerStats> players = new HashMap<>();

    @Getter @Setter private int gameDurationSec;

    @Getter @Setter private int wonTeam;
    
    @Setter @Getter private long startTime;
    
    public GameStats() {
        this.startTime = System.currentTimeMillis() / 1000;
        for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
            players.put(gp.getPlayer().getName(),
                    new GamePlayerStats(gp.getPlayer().getName(), gp.getClassName(), gp.getTeam()));
        }
    }
    
    public void addKill(GamePlayer target, GamePlayer killer) {
        if (target != null) {
            String name;
            GamePlayerStats stats;
            if (killer != null && !target.equals(killer)) {
                name = killer.getPlayer().getName();
                stats = players.get(name);
                stats.setKills(stats.getKills() + 1);
            }
            name = target.getPlayer().getName();
            stats = players.get(name);
            stats.setDeaths(stats.getDeaths() + 1);
        }
        if (Game.getInstance() instanceof DisplayInteractInterface) {
            ((DisplayInteractInterface) Game.getInstance()).updateDisplays();
        }
    }
    
    //counts advancement
    public void addKill(GamePlayer target, GamePlayer killer, Collection<GamePlayer> assists) {
        int killcnt = Game.getInstance().getRewardManager().get("killEnemy").getCount();
        GamePlayerStats stats;
        if (assists != null && !assists.isEmpty()) {
            double assistrew = killcnt * 0.4 / assists.size();
            for (GamePlayer player : assists) {
                String name = player.getPlayer().getName();
                stats = players.get(name);
                stats.setAssists(stats.getAssists() + 1);
                stats.setAdvancement(stats.getAdvancement() + assistrew);
            }
            stats = players.get(killer.getPlayer().getName());
            stats.setAdvancement(stats.getAdvancement() + killcnt - assistrew * assists.size());
        } else {
            stats = players.get(killer.getPlayer().getName());
            stats.setAdvancement(stats.getAdvancement() + killcnt);
        }
        
        stats = players.get(target.getPlayer().getName());
        stats.setAdvancement(stats.getAdvancement() - killcnt);
        addKill(target, killer);
    }
    
    public void addDamage(GamePlayer target, GamePlayer killer, double damage) {
        if (target != null) {
            String name;
            GamePlayerStats stats;
            if (killer != null && !target.equals(killer)) {
                name = killer.getPlayer().getName();
                stats = players.get(name);
                stats.setDamageDealt(stats.getDamageDealt() + damage);
            }
            name = target.getPlayer().getName();
            stats = players.get(name);
            stats.setDamageTaken(stats.getDamageTaken() + damage);
        }
    }
    
    public void addExp(GamePlayer gp, Integer value) {
        String name = gp.getPlayer().getName();
        GamePlayerStats stats = players.get(name);
        stats.setExpGained(stats.getExpGained() + value);
        
        if (Game.getInstance().getState().equals(Game.State.GAME) &&
                Game.getInstance() instanceof DisplayInteractInterface) {
            ((DisplayInteractInterface) Game.getInstance()).updateDisplays();
        }
    }
    
    
    public void addCapturePoints(String name, Integer points) {
        GamePlayerStats stats = players.get(name);
        stats.setCapturePoints(stats.getCapturePoints() + points);
    }
    
    private final LinkedList<FlyingText> texts = new LinkedList<>();
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
        players.clear();
        for (final FlyingText text : texts) {
            text.unload();
        }
    }
    
    private String getBestDamageDealt() {
        String best = "";
        double val = -1;
        for (GamePlayerStats entry : players.values()) {
            final String a = entry.getName();
            final double b = entry.getDamageDealt();
            if (val < b) {
                best = a;
                val = b;
            }
        }
        return best;
    }
    
    private String getBestDamageTaken() {
        String best = "";
        double val = Double.MAX_VALUE;
        for (GamePlayerStats entry : players.values()) {
            final String a = entry.getName();
            final double b = entry.getDamageTaken();
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
        for (GamePlayerStats entry : players.values()) {
            final String a = entry.getName();
            final int b = entry.getKills();
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
        for (GamePlayerStats entry : players.values()) {
            final String a = entry.getName();
            final int b = entry.getDeaths();
            if (val > b) {
                best = a;
                val = b;
            }
        }
        return best;
    }
}
