package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Random;

public class AfterParty {
    private final Game game;
    private final int winnerTeam;

    public AfterParty(Game game, int winnerTeam) {
        this.game = game;
        this.winnerTeam = winnerTeam;
    }

    public void start() {
        final Location loc = game.getInfo().getLobbyPosition().toLocation(game.getInfo().getLobbyWorld());

        game.getStats().spawnStats(loc.clone().add(0, 2, 4));
        for (GamePlayer gp : game.getPlayers().values()) {
            final Player player = gp.getPlayer();
            player.teleport(loc);
            gp.getItems().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
            MessagingUtils.sendActionBar("", player);
            if (gp.getTeam() == winnerTeam) {
                final int locationIndex = new Random().nextInt(game.getInfo().getWinnerPositions().length);
                final DirectedPosition spawnPosition = game.getInfo().getWinnerPositions()[locationIndex];
//                player.teleport(spawnPosition.toLocation(game.getInfo().getWorld()));
                MessagingUtils.sendTitle(Lang.get("game.won"), player, 0, 40, 0);
            } else {
                final int locationIndex = new Random().nextInt(game.getInfo().getLooserPositions().length);
                final DirectedPosition spawnPosition = game.getInfo().getLooserPositions()[locationIndex];
//                player.teleport(spawnPosition.toLocation(game.getInfo().getWorld()));
                MessagingUtils.sendTitle(Lang.get("game.lost"), player, 0, 40, 0);
            }
        }
    }

    public void stop() {

    }

    public Game getGame() {
        return game;
    }
}
