package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.GameInfo;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;

import static by.dero.gvh.model.Drawings.spawnFirework;

public class EtherCapture extends Game implements DisplayInteractInterface {
    private final EtherCaptureInfo etherCaptureInfo;
    private final EtherCollectorsManager collectorsManager;
    private static EtherCapture instance;

    private int[] currentEtherCount;

    public EtherCapture(GameInfo info, EtherCaptureInfo etherCaptureInfo) {
        super(info);
        instance = this;
        this.etherCaptureInfo = etherCaptureInfo;
        this.collectorsManager = new EtherCollectorsManager(this);
    }

    @Override
    public boolean load() {
        return super.load();
    }

    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.ether"), getInfo().getTeamCount() + 6);
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
        int cnt = getInfo().getTeamCount();
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            idxs.add(i);
        }
        idxs.sort((a, b) -> currentEtherCount[b] - currentEtherCount[a]);
        String[] str = new String[cnt + 6];
        for (int i = 0; i < cnt; ++i) {
            final int team = idxs.get(i);
            final String com = Lang.get("commands." + (char)('1' + team));
            str[i] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", currentEtherCount[team] +
                            " (" + (int) ((double) currentEtherCount[team] / etherCaptureInfo.getEtherToWin() * 100) + "%)");
        }
        for (final GamePlayer gp : getPlayers().values()) {
            str[cnt] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
            str[cnt+1] = " ";
//            str[cnt+2] = Lang.get("game.classSelected").replace("%class%", Lang.get("classes." + gp.getClassName()));
            String name = gp.getPlayer().getName();
            str[cnt+2] = Lang.get("game.expGained").replace("%exp%", String.valueOf(stats.getExpGained(name)));
            str[cnt+3] = Lang.get("game.kills").replace("%kills%", String.valueOf(stats.getKills(name)));
            str[cnt+4] = Lang.get("game.assists").replace("%assists%", String.valueOf(stats.getAssists(name)));
            str[cnt+5] = Lang.get("game.deaths").replace("%deaths%", String.valueOf(stats.getDeaths(name)));

            gp.getBoard().update(str);
        }
    }

    @Override
    protected void onPlayerRespawned(GamePlayer gp) {
        super.onPlayerRespawned(gp);
    }

    @Override
    public void start() {
        super.start();

        collectorsManager.load();
        final int teams = getInfo().getTeamCount();
        currentEtherCount = new int[teams];
        for (int index = 0; index < teams; ++index) {
            currentEtherCount[index] = 0;
        }

        setDisplays();
        updateDisplays();
    }

    @Override
    public boolean unload () {
        if (!loaded) {
            return false;
        }
        collectorsManager.unload();
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        return super.unload();
    }

    public void addEther(int team, int count) {
        currentEtherCount[team] += count;
        updateDisplays();
        checkForGameEnd();
    }

    private void checkForGameEnd() {
        for (int team = 0; team < getInfo().getTeamCount(); ++team) {
            if (currentEtherCount[team] >= etherCaptureInfo.getEtherToWin()) {
                World world = Minigame.getInstance().getWorld();
                world.playSound(getInfo().getLobbyPosition().toLocation(world).add(0, 30, 0),
                        Sound.ENTITY_ENDERDRAGON_DEATH, 300, 1);
                finish(team);
                return;
            }
        }
    }

    @Override
    public void onPlayerKilled(Player player, Player killer, Collection<Player> assists) {
        super.onPlayerKilled(player, killer, assists);
        addEther(getPlayers().get(killer.getName()).getTeam(), etherCaptureInfo.getEtherForKill());
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if (getState() != State.GAME) {
            return;
        }
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        final float exp = player.getExp();

        spawnFirework(player.getLocation().clone().add(0, 1, 0), 1);
        getPlayerDeathLocations().put(player.getName(), player.getLocation());
        spawnPlayer(getPlayers().get(player.getName()), getInfo().getRespawnTime());
        player.spigot().respawn();
        player.setExp(exp);
    }

    public EtherCaptureInfo getEtherCaptureInfo() {
        return etherCaptureInfo;
    }

    public int[] getCurrentEtherCount () {
        return currentEtherCount;
    }

    public static EtherCapture getInstance () {
        return instance;
    }
}
