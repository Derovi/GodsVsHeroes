package by.dero.gvh.lobby;

import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.WorldEditUtils;

public class PlayerLobby {
    private LobbyRecord record;

    public PlayerLobby(LobbyRecord record) {
        this.record = record;
    }

    public void create() {
        WorldEditUtils.pasteSchematic(Lobby.getInstance().getLobbySchematicFile(), Lobby.getInstance().getWorld(),
                record.getPosition());
    }

    public void destroy() {

    }

    public void load() {

    }

    public void unload() {

    }

    public LobbyRecord getRecord() {
        return record;
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

}
