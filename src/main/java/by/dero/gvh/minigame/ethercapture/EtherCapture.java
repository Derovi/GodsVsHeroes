package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.GameInfo;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
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
    public void load() {

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
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < getInfo().getTeamCount(); i++) {
            idxs.add(i);
        }
        idxs.sort((a, b) -> currentEtherCount[b] - currentEtherCount[a]);
        String[] str = new String[getInfo().getTeamCount() + 1];
        for (int i = 0; i < getInfo().getTeamCount(); ++i) {
            final int team = idxs.get(i);
            final String com = Lang.get("commands." + (char)('1' + team));
            str[i] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", currentEtherCount[team] +
                            " (" + (int) ((double) currentEtherCount[team] / etherCaptureInfo.getEtherToWin() * 100) + "%)");
        }
        for (final GamePlayer gp : getPlayers().values()) {
            str[getInfo().getTeamCount()] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
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
    public void finish(int winnerTeam) {
        super.finish(winnerTeam);
    }

    @Override
    public void unload () {
        collectorsManager.unload();
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        super.unload();
    }

    public void addEther(int team, int count) {
        currentEtherCount[team] += count;
        updateDisplays();
        checkForGameEnd();
    }

    private void checkForGameEnd() {
        for (int team = 0; team < getInfo().getTeamCount(); ++team) {
            if (currentEtherCount[team] >= etherCaptureInfo.getEtherToWin()) {
                int finalTeam = team;
                World world = Minigame.getInstance().getWorld();
                world.playSound(getInfo().getLobbyPosition().toLocation(world).add(0, 30, 0),
                        Sound.ENTITY_ENDERDRAGON_DEATH, 300, 1);
                Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> finish(finalTeam), 100);
                return;
            }
        }
    }

    @Override
    public void onPlayerKilled(Player player, Player killer) {
        super.onPlayerKilled(player, killer);
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
