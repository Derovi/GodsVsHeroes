package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

import static by.dero.gvh.model.Drawings.spawnFirework;
import static by.dero.gvh.utils.DataUtils.getPlayer;
import static by.dero.gvh.utils.DataUtils.getTargetEntity;

public class DeathMatch extends Game implements DisplayInteractInterface {
    private final DeathMatchInfo deathMatchInfo;

    private int[] currentLivesCount;

    public int[] getRespawning() {
        return respawning;
    }

    private int[] respawning;

    public DeathMatch(GameInfo info, DeathMatchInfo deathMatchInfo) {
        super(info);
        this.deathMatchInfo = deathMatchInfo;
        GameEvents.setGame(this);
    }

    @Override
    void load() {
        final int teams = getInfo().getTeamCount();
        currentLivesCount = new int[teams];
        for (int index = 0; index < teams; ++index) {
            currentLivesCount[index] = this.deathMatchInfo.getLivesCount();
        }
        respawning = new int[teams];
    }

    @Override
    public void setDisplays() {
        for (final GamePlayer gp : getPlayers().values()) {
            final Board board = new Board(Lang.get("game.livesLeft"),currentLivesCount.length + 1);
            gp.setBoard(board);
            final Scoreboard sb = board.getScoreboard();
            for (int team = 0; team < currentLivesCount.length; team++) {
                final String t = team + "hp";
                if (sb.getTeam(t) != null) {
                    sb.getTeam(t).unregister();
                }
                sb.registerNewTeam(t).setColor(
                        ChatColor.getByChar(Lang.get("commands." + (char)('1' + team)).charAt(1)));
            }
            if (sb.getObjective("health") != null) {
                sb.getObjective("health").unregister();
            }
            final Objective obj = sb.registerNewObjective("health", "health");
            obj.setDisplayName("§c❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (final GamePlayer othergp : getPlayers().values()) {
                final String name = othergp.getPlayer().getName();
                sb.getTeam(othergp.getTeam() + "hp").addEntry(name);
            }
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
        respawning[gp.getTeam()]--;
    }

    @Override
    public void start() {
        super.start();

        setDisplays();
        updateDisplays();
    }

    public DeathMatchInfo getDeathMatchInfo() {
        return deathMatchInfo;
    }

    @Override
    public void finish(int winnerTeam) {
        for (final GamePlayer gp : getPlayers().values()) {
            gp.getBoard().clear();
        }
        super.finish(winnerTeam);
    }

    private void checkForGameEnd() {
        boolean alive = false;
        int teamAlive = -1;
        Bukkit.getServer().broadcastMessage("kek");
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
        Bukkit.getServer().broadcastMessage("0");
        for (final GamePlayer gp : getPlayers().values()) {
            if (gp.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                Bukkit.getServer().broadcastMessage(teamAlive + " " + gp.getTeam());
                if (teamAlive == -1) {
                    teamAlive = gp.getTeam();
                } else {
                    if (teamAlive != gp.getTeam()) {
                        return;
                    }
                }
            }
        }

        Bukkit.getServer().broadcastMessage("1");
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

        spawnFirework(player.getLocation().clone().add(0,1,0), 1);

        final HashMap<LivingEntity, LivingEntity> damageCause = Minigame.getInstance().getGameEvents().getDamageCause();
        LivingEntity kil = damageCause.getOrDefault(player, player);
        if (player.getKiller() != null) {
            kil = player.getKiller();
        }

        onPlayerKilled(player, kil);
        getPlayerDeathLocations().put(player.getName(), player.getLocation());

        final int team = getPlayers().get(event.getEntity().getName()).getTeam();
        int respTime = -1;

        if (currentLivesCount[team] > 0) {
            --currentLivesCount[team];
            respawning[team]++;
            respTime = getInfo().getRespawnTime();
        }
        player.spigot().respawn();
        spawnPlayer(getPlayers().get(player.getName()), respTime);
        player.setExp(exp);

        damageCause.remove(player);
        updateDisplays();
        checkForGameEnd();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (getState() != State.GAME) {
            event.setCancelled(true);
        }
    }
}
