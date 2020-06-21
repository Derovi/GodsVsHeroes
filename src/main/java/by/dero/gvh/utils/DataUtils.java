package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.GameEvents;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.StorageInterface;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class DataUtils {
    private static Player lastUsedLightning;
    private static Long lastLightningTime;
    public static GamePlayer getPlayer(String name) {
        return Minigame.getInstance().getGame().getPlayers().get(name);
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer) {
        Minigame.getInstance().getGameEvents().getDamageCause().put(target, killer);
        double start = target.getHealth();
        target.damage(damage);
        if (start == target.getHealth()) {
            if (damage >= target.getHealth()) {
                target.setHealth(0);
            } else {
                target.setHealth(target.getHealth() - damage);
            }
        }
    }

    public static boolean isEnemy(final Entity ent, final int team) {
        if (!(ent instanceof LivingEntity) || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof Player)) {
            return true;
        }
        return getPlayer(ent.getName()).getTeam() != team;
    }

    public static List<LivingEntity> getNearby(final Location wh, final double radius) {
        final List<LivingEntity> buf = new ArrayList<>();
        for (Entity ent : Objects.requireNonNull(wh.getWorld()).getNearbyEntities(wh, radius, radius, radius)) {
            if (ent instanceof LivingEntity &&
                    ent.getLocation().distance(wh) <= radius) {
                buf.add((LivingEntity) ent);
            }
        }
        return buf;
    }

    public static boolean isAlly(final Entity ent, final int team) {
        if (!(ent instanceof LivingEntity) || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof Player)) {
            return true;
        }
        return getPlayer(ent.getName()).getTeam() == team;
    }

    public static String loadOrDefault(StorageInterface storage, String collection, String name, String defaultObject) throws IOException {
        if (!storage.exists(collection, name)) {
            storage.save(collection, name, defaultObject);
        }
        return storage.load(collection, name);
    }

    public static Player getLastUsedLightning() {
        return lastUsedLightning;
    }

    public static void setLastUsedLightning(Player lastUsedLightning) {
        DataUtils.lastUsedLightning = lastUsedLightning;
        DataUtils.lastLightningTime = System.currentTimeMillis();
    }

    public static Long getLastLightningTime() {
        return lastLightningTime;
    }
}
