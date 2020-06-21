package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;

import java.util.Random;

public class AfterParty {
    private final Game game;
    private final int winnerTeam;

    public AfterParty(Game game, int winnerTeam) {
        this.game = game;
        this.winnerTeam = winnerTeam;
    }

    public void start() {
        for (GamePlayer player : game.getPlayers().values()) {
            player.getItems().clear();
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getPlayer().getInventory().clear();
            player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
            player.getPlayer().setHealth(10);
            MessagingUtils.sendActionBar("", player.getPlayer());
            if (player.getTeam() == winnerTeam) {
                final int locationIndex = new Random().nextInt(game.getInfo().getWinnerPositions().length);
                final DirectedPosition spawnPosition = game.getInfo().getWinnerPositions()[locationIndex];
                player.getPlayer().teleport(spawnPosition.toLocation(game.getInfo().getWorld()));
                MessagingUtils.sendTitle(Lang.get("game.won"), player.getPlayer(), 0, 40, 0);
            } else {
                final int locationIndex = new Random().nextInt(game.getInfo().getLooserPositions().length);
                final DirectedPosition spawnPosition = game.getInfo().getLooserPositions()[locationIndex];
                player.getPlayer().teleport(spawnPosition.toLocation(game.getInfo().getWorld()));
                MessagingUtils.sendTitle(Lang.get("game.lost"), player.getPlayer(), 0, 40, 0);
            }
        }
    }

    public void stop() {

    }

    public Game getGame() {
        return game;
    }
}
