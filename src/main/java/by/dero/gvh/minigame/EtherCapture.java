package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

import static by.dero.gvh.model.Drawings.spawnFirework;

public class EtherCapture extends Game implements DisplayInteractInterface {
    private final EtherCaptureInfo etherCaptureInfo;

    private int[] currentEtherCount;

    public EtherCapture(GameInfo info, EtherCaptureInfo etherCaptureInfo) {
        super(info);
        this.etherCaptureInfo = etherCaptureInfo;
    }

    @Override
    void load() {
        final int teams = getInfo().getTeamCount();
        currentEtherCount = new int[teams];
        for (int index = 0; index < teams; ++index) {
            currentEtherCount[index] = 0;
        }
    }

    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.ether"),getInfo().getTeamCount() + 1);
            final Scoreboard sb = board.getScoreboard();
            for (int team = 0; team < getInfo().getTeamCount(); team++) {
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
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < getInfo().getTeamCount(); i++) {
            indexes.add(i);
        }
        //idxs.sort((a, b) -> currentLivesCount[b] - currentLivesCount[a]);
        String[] str = new String[getInfo().getTeamCount() + 1];
        for (int idx = 0; idx < getInfo().getTeamCount(); ++idx) {
            final int team = indexes.get(idx);
            final String com = Lang.get("commands." + (char)('1' + team));
            str[team] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", String.valueOf(currentEtherCount[team]) +
                            " (" + (int) ((double) currentEtherCount[team] / etherCaptureInfo.getEtherToWin() * 100) + "%)");
        }
        for (final GamePlayer gp : getPlayers().values()) {
            str[getInfo().getTeamCount()] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
            gp.getBoard().update(str);
        }
    }

    @Override
    protected void onPlayerRespawned(GamePlayer gp) {}

    @Override
    public void start() {
        super.start();

        setDisplays();
        updateDisplays();
    }

    @Override
    public void finish(int winnerTeam) {
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        super.finish(winnerTeam);
    }

    public void addEther(int team, int count) {
        currentEtherCount[team] += count;
        updateDisplays();
        checkForGameEnd();
    }

    private void checkForGameEnd() {
        for (int team = 0; team < getInfo().getTeamCount(); ++team) {
            if (currentEtherCount[team] >= etherCaptureInfo.getEtherToWin()) {
                finish(team);
            }
        }
    }

    @Override
    public void onPlayerKilled(Player player, LivingEntity killer) {
        super.onPlayerKilled(player, killer);
        if (killer instanceof Player) {
            addEther(getPlayers().get(killer.getName()).getTeam(), etherCaptureInfo.getEtherForKill());
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if (getState() != State.GAME) {
            return;
        }
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        final float exp = player.getExp();

        spawnFirework(player.getLocation().clone().add(0,1,0), 1);

        final int team = getPlayers().get(player.getName()).getTeam();
        int respTime = -1;

        /*if (currentLivesCount[team] > 0) {
            --currentLivesCount[team];
            respawning[team]++;
            respTime = getInfo().getRespawnTime();
        }*/
        getPlayerDeathLocations().put(player.getName(), player.getLocation());
        player.spigot().respawn();
        spawnPlayer(getPlayers().get(player.getName()), respTime);
        player.setExp(exp);
    }
}
