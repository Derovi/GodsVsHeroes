package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;

public class Lobby {
    private Game game;

    public void onPlayerJoined(GamePlayer gamePlayer) {

    }

    public void onPlayerLeft(GamePlayer gamePlayer) {

    }

    public Lobby(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
