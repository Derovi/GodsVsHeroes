package by.dero.gvh.game;

public class GameInfo {
    private String world = "World";
    private int teamCount = 2;
    private int respawnTime = 10;
    private int minPlayerCount = 2;
    private int maxPlayerCount = 24;
    private Position lobbyPosition;
    private Position[][] spawnPoints;

    public Position getLobbyPosition() {
        return lobbyPosition;
    }

    public void setLobbyPosition(Position lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getMinPlayerCount() {
        return minPlayerCount;
    }

    public void setMinPlayerCount(int minPlayerCount) {
        this.minPlayerCount = minPlayerCount;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public Position[][] getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(Position[][] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}
