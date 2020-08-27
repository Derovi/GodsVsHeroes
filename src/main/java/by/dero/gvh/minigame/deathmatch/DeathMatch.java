package by.dero.gvh.minigame.deathmatch;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.GameInfo;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.HeroLevel;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;

public class DeathMatch extends Game implements DisplayInteractInterface {
    private final DeathMatchInfo deathMatchInfo;

    private int[] currentLivesCount;

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
        return true;
    }
    
    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.deathMatch"), getInfo().getTeamCount() + 8);
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
        int cnt = getInfo().getTeamCount();
        ArrayList<Integer> idxs = new ArrayList<>(cnt);
        for (int i = 0; i < cnt; i++) {
            idxs.add(i);
        }
        idxs.sort((a, b) -> currentLivesCount[b] - currentLivesCount[a]);
        String[] str = new String[cnt + 8];
        for (int zxc = 0; zxc < cnt; zxc++) {
            final int i = idxs.get(zxc);
            final String com = Lang.get("commands." + (char)('1' + i));
            str[zxc] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
                    .replace("%com%", com)
                    .replace("%pts%", String.valueOf(currentLivesCount[i]));
        }
        str[cnt] = " ";
        str[cnt+6] = " ";
        str[cnt+7] = Lang.get("game.online").replace("%online%", String.valueOf(getPlayers().size()));
        for (final GamePlayer gp : getPlayers().values()) {
            GamePlayerStats stats = getStats().getPlayers().get(gp.getPlayer().getName());
            str[cnt+1] = Lang.get("commands.playingFor").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
//            str[cnt+2] = Lang.get("game.classSelected").replace("%class%", Lang.get("classes." + gp.getClassName()));
            double mult = getMultiplier(gp);
            str[cnt+2] = (mult == 1 ? Lang.get("game.expGained") : Lang.get("game.expGainedMult").
                    replace("%mul%", String.format("%.1f", mult))).replace("%exp%", GameUtils.getString(stats.getExpGained()));
            str[cnt+3] = Lang.get("game.kills").replace("%kills%", String.valueOf(stats.getKills()));
            str[cnt+4] = Lang.get("game.deaths").replace("%deaths%", String.valueOf(stats.getDeaths()));
            str[cnt+5] = Lang.get("game.assists").replace("%assists%", String.valueOf(stats.getAssists()));
            gp.getBoard().update(str);
        }
    }

    @Override
    protected void onPlayerRespawned(final GamePlayer gp) {
        super.onPlayerRespawned(gp);
    }
    
    @Override
    public void onPlayerKilled(GamePlayer player, GamePlayer killer, Collection<GamePlayer> assists) {
        super.onPlayerKilled(player, killer, assists);
        try {
            if (!player.equals(killer)) {
                getRewardManager().give("killEnemy", killer.getPlayer(), "");
    
                MessagingUtils.sendSubtitle(Lang.get("rewmes.kill").
                                replace("%exp%", GameUtils.getString(getMultiplier(killer) * getRewardManager().get("killEnemy").getCount())),
                        killer.getPlayer(), 0, 20, 0);
                
                String kilCode = GameUtils.getTeamColor(killer.getTeam());
                String tarCode = GameUtils.getTeamColor(player.getTeam());
                String kilClass = Lang.get("classes." + killer.getClassName()) +
                        " " + new HeroLevel(killer.getPlayerInfo(), killer.getClassName()).getRomeLevel();
                String tarClass = Lang.get("classes." + player.getClassName()) +
                        " " + new HeroLevel(player.getPlayerInfo(), player.getClassName()).getRomeLevel();
                Bukkit.getServer().broadcastMessage(Lang.get("game.killGlobalMessage").
                        replace("%kilCode%", kilCode).replace("%kilClass%", kilClass).
                        replace("%killer%", killer.getPlayer().getName()).
                        replace("%tarCode%", tarCode).replace("%tarClass%", tarClass).
                        replace("%target%", player.getPlayer().getName()));
            
                if (assists != null) {
                    for (GamePlayer pl : assists) {
                        getRewardManager().give("assist", pl.getPlayer(), "");
                        MessagingUtils.sendSubtitle(Lang.get("rewmes.assist").
                                        replace("%exp%", GameUtils.getString(getMultiplier(pl) * getRewardManager().get("assist").getCount())),
                                pl.getPlayer(), 0, 20, 0);
                    }
                }
                getGameStatsManager().addKill(player, killer, assists);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        int teamAlive = -1;
        for (int i = 0; i < getInfo().getTeamCount(); i++) {
            if (currentLivesCount[i] > 0) {
                if (teamAlive != -1) {
                    return;
                }
                teamAlive = i;
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
        final float exp = player.getExp();

        final int team = getPlayers().get(player.getName()).getTeam();
        int respTime = getInfo().getRespawnTime();

        --currentLivesCount[team];
    
        player.spigot().respawn();
        spawnPlayer(getPlayers().get(player.getName()), respTime);
        player.setExp(exp);

        updateDisplays();
        checkForGameEnd();
    }
}
