package by.dero.gvh.minigame;

import by.dero.gvh.model.Lang;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

public class MapVoting {
    public static class Map {
        @Getter @Setter
        String displayName;
        @Getter @Setter
        String buildName;
        @Getter @Setter
        int voteCount = 0;

        public void vote() {
            voteCount++;
        }

        public Map(String displayName, String buildName) {
            this.displayName = displayName;
            this.buildName = buildName;
        }
    }

    @Getter
    public List<Map> maps = new ArrayList<>();
    public Set<UUID> votedPlayers = new HashSet<>();

    public MapVoting() {
        maps.add(new Map("Japan", "Japan"));
        maps.add(new Map("Castle", "Castle"));
    }

    public Map getMap(String displayName) {
        for (Map map : maps) {
            if (map.displayName.equals(displayName)) {
                return map;
            }
        }
        return null;
    }

    public boolean isMapExists(String displayName) {
        return getMap(displayName) != null;
    }

    public void vote(Player player, String mapName) {
        Map map = getMap(mapName);
        if (map == null) {
            player.sendMessage(Lang.get("mapVoting.notExists").replace("%map%", mapName));
            return;
        }
        if (votedPlayers.contains(player.getUniqueId())) {
            player.sendMessage(Lang.get("mapVoting.alreadyVoted"));
            return;
        }
        votedPlayers.add(player.getUniqueId());
        map.vote();
        player.sendMessage(Lang.get("mapVoting.sucVoted").replace("%map%", mapName));
    }

    public Map getMostVoted() {
        Map mostVoted = null;
        for (Map map : maps) {
            if (mostVoted == null || mostVoted.getVoteCount() < map.getVoteCount()) {
                mostVoted = map;
            }
        }
        return mostVoted;
    }
}
