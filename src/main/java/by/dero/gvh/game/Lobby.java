package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;

public class Lobby {
    private final Game game;

    public Lobby(Game game) {
        this.game = game;
    }

    public void startGame() {

        game.start();
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        if (game.getPlayers().size() >= game.getInfo().getMinPlayerCount()) {
            game.start();
            Plugin.getInstance().getServer().broadcastMessage("§aGame started!");
        }
        Plugin.getInstance().getServer().broadcastMessage("§aPlayer " + gamePlayer.getPlayer().getName() + " joined! " +
                game.getPlayers().size() + '/' + game.getInfo().getMinPlayerCount());
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        Plugin.getInstance().getServer().broadcastMessage("§aPlayer " + gamePlayer.getPlayer().getName() + " left! " +
                (game.getPlayers().size() - 1) + '/' + game.getInfo().getMinPlayerCount());
    }

    public Game getGame() {
        return game;
    }
}
