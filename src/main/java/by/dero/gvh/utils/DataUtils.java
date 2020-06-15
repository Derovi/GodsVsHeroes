package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Minigame;
import by.dero.gvh.model.StorageInterface;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DataUtils {
    public static GamePlayer getPlayer(String name) {
        return Minigame.getInstance().getGame().getPlayers().get(name);
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
}
