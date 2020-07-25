package by.dero.gvh.minigame;

import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import lombok.Getter;
import lombok.Setter;

public class GameInfo {
    @Getter @Setter private String lobbyWorld = "World";
    @Getter @Setter private String mode;
    @Getter @Setter private int teamCount = 2;
    @Getter @Setter private int respawnTime = 200;
    @Getter @Setter private int minPlayerCount = 2;
    @Getter @Setter private int maxPlayerCount = 24;
    @Getter @Setter private int afkTime = 2400;
    @Getter @Setter private DirectedPosition lobbyPosition;
    @Getter @Setter private DirectedPosition[][] spawnPoints;
    @Getter @Setter private int finishTime;
    @Getter @Setter private DirectedPosition[] winnerPositions;
    @Getter @Setter private DirectedPosition[] looserPositions;
    @Getter @Setter private DirectedPosition[] mapBorders;
    @Getter @Setter private Position[] healPoints;
    @Getter @Setter private Position[] speedPoints;
    @Getter @Setter private Position[] resistancePoints;
}
