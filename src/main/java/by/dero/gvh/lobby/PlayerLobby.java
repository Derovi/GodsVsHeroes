package by.dero.gvh.lobby;

import by.dero.gvh.lobby.monuments.Monument;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerLobby {
    private final LobbyRecord record;
    private final Player player;
    private final HashMap<String, Monument> monuments = new HashMap<>();

    public PlayerLobby(LobbyRecord record) {
        this.record = record;
        this.player = Bukkit.getPlayer(record.getOwner());
    }

    public void create() {
        WorldEditUtils.pasteSchematic(Lobby.getInstance().getLobbySchematicFile(),
                Lobby.getInstance().getWorld(), record.getPosition());

    }

    public void destroy() {

    }

    public void load() {
        for (Map.Entry<String, Position> entry :
                Lobby.getInstance().getInfo().getClassNameToMonumentPosition().entrySet()) {
            try {
                String monumentName = entry.getKey();
                Monument monument = Lobby.getInstance().getMonumentManager().getClassNameToMonument().
                        get(monumentName).getConstructor(Position.class, String.class, Player.class).
                        newInstance(transformFromLobbyCord(entry.getValue()), monumentName, player);
                monument.load();
                monuments.put(monumentName, monument);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void unload() {
        for (Monument monument : monuments.values()) {
            monument.unload();
        }
    }

    public LobbyRecord getRecord() {
        return record;
    }

    public Player getPlayer() {
        return player;
    }

    public Position transformToLobbyCord(Position position) {
        return new Position(position.getX() - record.getPosition().getX(),
                position.getY() - record.getPosition().getY(),
                position.getZ() - record.getPosition().getZ());
    }

    public Position transformFromLobbyCord(Position position) {
        return new Position(position.getX() + record.getPosition().getX(),
                position.getY() + record.getPosition().getY(),
                position.getZ() + record.getPosition().getZ());
    }

    public HashMap<String, Monument> getMonuments() {
        return monuments;
    }
}
