package by.dero.gvh.minigame;

import by.dero.gvh.utils.DirectedPosition;

public class GameInfo {
    private String lobbyWorld = "World";
    private String mode;
    private int teamCount = 2;
    private int respawnTime = 200;
    private int minPlayerCount = 2;
    private int maxPlayerCount = 24;
    private DirectedPosition lobbyPosition;
    private DirectedPosition[][] spawnPoints;
    private int finishTime;
    private DirectedPosition[] winnerPositions;
    private DirectedPosition[] looserPositions;
    private DirectedPosition[] mapBorders;
    private DirectedPosition[] healPoints;
    private DirectedPosition[] speedPoints;
    private DirectedPosition[] resistancePoints;
    private DirectedPosition[] liftHints;

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

    public String getLobbyWorld() {
        return lobbyWorld;
    }

    public void setLobbyWorld(String lobbyWorld) {
        this.lobbyWorld = lobbyWorld;
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

    public DirectedPosition[] getMapBorders() {
        return mapBorders;
    }

    public void setMapBorders(DirectedPosition[] mapBorders) {
        this.mapBorders = mapBorders;
    }

    public DirectedPosition[] getHealPoints() {
        return healPoints;
    }

    public void setHealPoints(DirectedPosition[] healPoints) {
        this.healPoints = healPoints;
    }

    public DirectedPosition[] getSpeedPoints() {
        return speedPoints;
    }

    public void setSpeedPoints(DirectedPosition[] speedPoints) {
        this.speedPoints = speedPoints;
    }

    public DirectedPosition[] getResistancePoints() {
        return resistancePoints;
    }

    public void setResistancePoints(DirectedPosition[] resistancePoints) {
        this.resistancePoints = resistancePoints;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public DirectedPosition[] getLiftHints () {
        return liftHints;
    }

    public void setLiftHints (DirectedPosition[] liftHints) {
        this.liftHints = liftHints;
    }
}
