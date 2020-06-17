package by.dero.gvh.lobby;

import by.dero.gvh.utils.Position;

public class LobbyRecord {
    private int version;
    private Position position;
    private String owner;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
