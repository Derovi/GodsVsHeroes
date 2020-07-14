package by.dero.gvh.minigame.deathmatch;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.GameInfo;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class DeathMatch extends Game implements DisplayInteractInterface {
    private final DeathMatchInfo deathMatchInfo;

    private int[] currentLivesCount;

    private int[] respawning;

    public DeathMatch(GameInfo info, DeathMatchInfo deathMatchInfo) {
        super(info);
        this.deathMatchInfo = deathMatchInfo;
    }

    @Override
    public boolean load() {
        if (!super.load()) {
            return false;
        }
        final int teams = getInfo().getTeamCount();
        currentLivesCount = new int[teams];
        for (int index = 0; index < teams; ++index) {
            currentLivesCount[index] = this.deathMatchInfo.getLivesCount();
        }
        respawning = new int[teams];
        return true;
    }

    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.livesLeft"),currentLivesCount.length + 1);
            final Scoreboard sb = board.getScoreboard();
            for (int team = 0; team < currentLivesCount.length; team++) {
                final String t = team + "hp";
                Team currentTeam = sb.registerNewTeam(t);
                currentTeam.setPrefix(Lang.get("commands." + (char)('1' + team)).substring(0, 2));
                currentTeam.setCanSeeFriendlyInvisibles(false);
            }
            final Objective obj = sb.registerNewObjective("health", "health");
            obj.setDisplayName("§c❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (final GamePlayer othergp : getPlayers().values()) {
                final String name = othergp.getPlayer().getName();
                sb.getTeam(othergp.getTeam() + "hp").addEntry(name);
            }
            gp.setBoard(board);
        }
    }

    @Override
    public void updateDisplays() {
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < currentLivesCount.length; i++) {
            idxs.add(i);
        }
        idxs.sort((a, b) -> currentLivesCount[b] - currentLivesCount[a]);
        String[] str = new String[currentLivesCount.length + 1];
        for (int zxc = 0; zxc < currentLivesCount.length; zxc++) {
            final int i = idxs.get(zxc);
            final String com = Lang.get("commands." + (char)('1' + i));
            str[i] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", String.valueOf(currentLivesCount[i]));
        }
        for (final GamePlayer gp : getPlayers().values()) {
            str[currentLivesCount.length] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
            gp.getBoard().update(str);
        }
    }

    @Override
    protected void onPlayerRespawned(final GamePlayer gp) {
        super.onPlayerRespawned(gp);
        respawning[gp.getTeam()]--;
    }

    @Override
    public void start() {
        super.start();

        setDisplays();
        updateDisplays();
    }

    @Override
    public void finish(int winnerTeam) {
        super.finish(winnerTeam);
    }

    @Override
    public boolean unload () {
        if (!loaded) {
            return false;
        }
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        return super.unload();
    }

    private void checkForGameEnd() {
        boolean alive = false;
        int teamAlive = -1;
        for (final int i : currentLivesCount) {
            if (i > 0) {
                if (alive) {
                    return;
                }
                alive = true;

            }
        }
        for (final int i : respawning) {
            if (i > 0) {
                return;
            }
        }
        for (final GamePlayer gp : getPlayers().values()) {
            if (GameUtils.isInGame(gp.getPlayer())) {
                if (teamAlive == -1) {
                    teamAlive = gp.getTeam();
                } else {
                    if (teamAlive != gp.getTeam()) {
                        return;
                    }
                }
            }
        }

        finish(teamAlive);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if (getState() != State.GAME) {
            return;
        }
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        //player.leaveVehicle();
        final float exp = player.getExp();

        Drawings.spawnFirework(player.getLocation().clone().add(0,1,0), 1);

        final int team = getPlayers().get(player.getName()).getTeam();
        int respTime = -1;

        if (currentLivesCount[team] > 0) {
            --currentLivesCount[team];
            respawning[team]++;
            respTime = getInfo().getRespawnTime();
        }
        getPlayerDeathLocations().put(player.getName(), player.getLocation());
        player.spigot().respawn();
        spawnPlayer(getPlayers().get(player.getName()), respTime);
        player.setExp(exp);

        updateDisplays();
        checkForGameEnd();
    }
}
