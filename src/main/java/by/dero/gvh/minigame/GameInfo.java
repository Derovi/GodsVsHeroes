package by.dero.gvh.minigame;

import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;

public class GameInfo {
    private String world = "World";
    private int teamCount = 2;
    private int respawnTime = 10;
    private int minPlayerCount = 2;
    private int maxPlayerCount = 24;
    private DirectedPosition lobbyPosition;
    private DirectedPosition[][] spawnPoints;
    private int finishTime;
    private DirectedPosition[] winnerPositions;
    private DirectedPosition[] looserPositions;

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public DirectedPosition[] getWinnerPositions() {
        return winnerPositions;
    }

    public void setWinnerPositions(DirectedPosition[] winnerPositions) {
        this.winnerPositions = winnerPositions;
    }

    public DirectedPosition[] getLooserPositions() {
        return looserPositions;
    }

    public void setLooserPositions(DirectedPosition[] looserPositions) {
        this.looserPositions = looserPositions;
    }

    public DirectedPosition getLobbyPosition() {
        return lobbyPosition;
    }

    public void setLobbyPosition(DirectedPosition lobbyPosition) {
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

    public DirectedPosition[][] getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(DirectedPosition[][] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}
