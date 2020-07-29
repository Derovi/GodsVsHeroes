package by.dero.gvh;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Booster;
import by.dero.gvh.model.BoosterInfo;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BoosterManager {
    @Getter
    private final HashMap<String, BoosterInfo> boosters = new HashMap<>();
    @Getter
    private final HashMap<UUID, List<Booster>> playerBoosters = new HashMap<>();
    @Getter
    private final HashMap<UUID, Double> playerMultipliers = new HashMap<>();

    public BoosterManager() {
        boosters.put("L1", BoosterInfo.builder()
                .name("L1")
                .durationSec(3600)
                .selfMultiplier(2)
                .cost(59)
                .build());
        boosters.put("L2", BoosterInfo.builder()
                .name("L2")
                .durationSec(7200)
                .selfMultiplier(2)
                .cost(99)
                .build());
        boosters.put("L3", BoosterInfo.builder()
                .name("L3")
                .durationSec(3600)
                .selfMultiplier(3)
                .cost(129)
                .build());
        boosters.put("L4", BoosterInfo.builder()
                .name("L4")
                .durationSec(3600)
                .selfMultiplier(5)
                .cost(239)
                .build());
        boosters.put("L5", BoosterInfo.builder()
                .name("L5")
                .durationSec(-1)
                .cost(259)
                .build());

        boosters.put("G1", BoosterInfo.builder()
                .name("G1")
                .durationSec(3600)
                .teamMultiplier(2)
                .cost(149)
                .build());
        boosters.put("G2", BoosterInfo.builder()
                .name("G2")
                .durationSec(7200)
                .selfMultiplier(3)
                .teamMultiplier(1.5)
                .cost(249)
                .build());
        boosters.put("G3", BoosterInfo.builder()
                .name("G3")
                .durationSec(3600)
                .gameMultiplier(2)
                .cost(219)
                .build());
    }

    public List<Booster> getBoosters(Player player) {
        if (playerBoosters.containsKey(player.getUniqueId())) {
            return playerBoosters.get(player.getUniqueId());
        }
        return Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getBoosters();
    }

    public double calculateMultiplier(Game game, GamePlayer gp) {
        double result = 1;
        long currentTime = System.currentTimeMillis() / 1000;
        for (GamePlayer other : game.getPlayers().values()) {
            List<Booster> playerBoosters = getBoosters(other.getPlayer());
            for (Booster booster : playerBoosters) {
                if (booster.getExpirationTime() < currentTime || booster.getStartTime() > currentTime) {
                    continue;
                }
                BoosterInfo info = boosters.get(booster.getName());
                result += info.getGameMultiplier() - 1;
                if (other.getTeam() == gp.getTeam()) {
                    result += info.getTeamMultiplier() - 1;
                }
                if (other.getPlayer().getUniqueId().equals(gp.getPlayer().getUniqueId())) {
                    result += info.getSelfMultiplier() + booster.getBonus() - 1;
                }
            }
        }
        return result;
    }

    public void precalcMultipliers(Game game) {
        for (GamePlayer gp : game.getPlayers().values()) {
            playerMultipliers.put(gp.getPlayer().getUniqueId(), calculateMultiplier(game, gp));
        }
    }

    public void load(Collection<? extends Player> players) {
        for (Player player : players) {
            playerBoosters.put(player.getUniqueId(),
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getBoosters());
        }
    }

    public double getMultiplier(Game game, GamePlayer gp) {
        if (playerMultipliers.containsKey(gp.getPlayer().getUniqueId())) {
            return playerMultipliers.get(gp.getPlayer().getUniqueId());
        }
        return calculateMultiplier(game, gp);
    }

    public static void removeExpiredBoosters(List<Booster> boosters) {
        long currentTime = System.currentTimeMillis() / 1000;
        boosters.removeIf((booster) -> (booster.getExpirationTime() <= currentTime && !booster.getName().equals("L5")));
    }
}
