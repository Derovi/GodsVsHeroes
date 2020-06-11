package by.dero.gvh.game;

import com.google.gson.GsonBuilder;
public class GameInfo {
    String world = "World";
    int teamCount = 2;
    int minPlayerCount = 2;
    int maxPlayerCount = 24;
    int livesCount = 20;
    Position[][] spawnPoints = {{new Position(50,65,50), new Position(20,65,40)},
            {new Position(250,65,250), new Position(220,65,240)}};

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

    public int getLivesCount() {
        return livesCount;
    }

    public void setLivesCount(int livesCount) {
        this.livesCount = livesCount;
    }

    public Position[][] getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(Position[][] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}
