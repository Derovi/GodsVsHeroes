package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.stats.PlayerStats;
import by.dero.gvh.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftTeam;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import ru.cristalix.core.display.data.DataDrawData;
import ru.cristalix.core.display.data.StringDrawData;
import ru.cristalix.core.math.V2;

import java.util.*;

public class PlayerLobby {
    @Getter private final Player player;
    private DataDrawData data;
    @Getter private Runnable scoreboardUpdater;
    @Getter @Setter
    private PlayerStats stats;

    @Getter private final List<BukkitRunnable> runnables = new ArrayList<>();

    public PlayerLobby(Player player) {
        this.player = player;
    }

    public void create() {
        // TODO create lobby
    }

    public void destroy() {

    }

    public Pair<String, DirectedPosition> getPortal() {
        DirectedPosition pos = new DirectedPosition(player.getLocation().add(0, 1.5, 0));
        for (Map.Entry<String, DirectedPosition> portal : Lobby.getInstance().getInfo().getPortals().entrySet()) {
            DirectedPosition port = portal.getValue();
            if (port.distance(pos) < 1) {
                return Pair.of(portal);
            }
        }
        return null;
    }

    public void load() {
        stats = Plugin.getInstance().getGameStatsData().getPlayerStats(player.getName());
        for (DirectedPosition portal : Lobby.getInstance().getInfo().getPortals().values()) {
            loadPortal(portal);
        }
        loadBoard();
        loadSelectedClass();
    }

    private void loadSelectedClass() {
        //selectedClass = new FlyingText(
        //        Lobby.getInstance().getInfo().getPortalPosition().toLocation(Lobby.getInstance().getWorld()).add(0,2,0), "");
//
//        Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), () -> {
//            data.setStrings(getStrings("lol"));
//            IRenderService.get().setRenderVisible(world.getUID(), name, false);
//            IRenderService.get().setRenderVisible(world.getUID(), name, true);
//        }, 20, 20);
    }

    private static List<StringDrawData> getStrings(String className) {
        return Collections.singletonList(
                StringDrawData.builder().align(1).scale(4).string(className).position(new V2(135, 10)).build()
        );
    }

    private void loadBoard() {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective obj = scoreboard.registerNewObjective("Lobby", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        final CraftTeam[] teams = new CraftTeam[5];
        for (int i = 0; i < 5; i++) {
            teams[i] = (CraftTeam) scoreboard.registerNewTeam("" + i);
            final String x = "§" + (char)('a' + i);
            teams[i].addEntry(x);
            obj.getScore(x).setScore(5-i);
        }

        player.setScoreboard(scoreboard);
        scoreboardUpdater = () -> {
            if (stats == null) {
                return;
            }
            final PlayerInfo info = Plugin.getInstance().getPlayerData().getStoredPlayerInfo(player.getName());
            float exp = Math.max(0, Math.min(1, (float) stats.getLevel().getExpOnThisLevel() / stats.getLevel().getExpToNextLevel()));
            player.setExp(exp);
            player.setLevel(stats.getLevel().getLevel());
            Board.setText(teams[0], Lang.get("lobby.selectedClass")
                    .replace("%class%", Lang.get("classes." + info.getSelectedClass())));
            Board.setText(teams[1], Lang.get("lobby.level").replace("%val%", String.valueOf(stats.getLevel().getLevel())));
            Board.setText(teams[2], Lang.get("lobby.moneyBalance")
                    .replace("%money%", String.valueOf(info.getBalance())));
            Board.setText(teams[3], " ");
            Board.setText(teams[4], Lang.get("lobby.online")
                    .replace("%online%", String.valueOf(Plugin.getInstance().getServerData().getSavedOnline())));
        };
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                } else {
                    scoreboardUpdater.run();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 20, 20);
    }

    private void loadPortal(DirectedPosition portal) {
        BukkitRunnable runnable = new BukkitRunnable() {
            double angle = 0;
//            final double portalPitch = portal.getPitch();
            final double turnsPerSec = 0.25;
            final double radius = 1.2;
            final int parts = 3;
            final Location center = portal.toLocation(Lobby.getInstance().getWorld());
            @Override
            public void run() {
                for (int i = 0; i < parts; i++) {
                    double cur = angle + MathUtils.PI2 * i / parts;
                    Vector kek = MathUtils.rotateAroundAxis(new Vector(0, radius, 0), portal.getDirection(), cur);
//                    Vector at = new Vector(0, MathUtils.sin(cur) * radius, MathUtils.cos(cur) * radius);
//                    MathUtils.rotateAroundAxis(at, MathUtils.UPVECTOR, portalPitch);
                    player.spawnParticle(Particle.FLAME, center.clone().add(kek), 0, 0, 0, 0);
                }
                angle = (angle + MathUtils.PI2 * turnsPerSec / 20) % MathUtils.PI2;
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);
        runnables.add(runnable);
    }

    public void unload() {
        for (BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        //selectedClass.unload();
    }
}
