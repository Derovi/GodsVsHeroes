package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.cristalix.core.display.data.DataDrawData;
import ru.cristalix.core.display.data.StringDrawData;
import ru.cristalix.core.math.V2;
import ru.cristalix.core.math.V3;
import ru.cristalix.core.render.IRenderService;
import ru.cristalix.core.render.VisibilityTarget;
import ru.cristalix.core.render.WorldRenderData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static by.dero.gvh.utils.Board.setText;

public class PlayerLobby {
    private final Player player;
    private DataDrawData data;
    private Runnable scoreboardUpdater;

    private final List<BukkitRunnable> runnables = new ArrayList<>();

    public PlayerLobby(Player player) {
        this.player = player;
    }

    public void create() {
        // TODO create lobby
    }

    public void destroy() {

    }

    public boolean isInPortal() {
        final Position pos = new Position(player.getLocation());
        final Position portal = Lobby.getInstance().getInfo().getPortalPosition();
        return pos.distance(portal) < 2 &&
                Math.abs(pos.getZ() - portal.getZ()) < 1;
    }

    public void load() {
        loadPortal();
        loadBoard();
        loadSelectedClass();
    }

    private void loadSelectedClass() {
        //selectedClass = new FlyingText(
        //        Lobby.getInstance().getInfo().getPortalPosition().toLocation(Lobby.getInstance().getWorld()).add(0,2,0), "");
        World world = Lobby.getInstance().getWorld();
        Position pos = Lobby.getInstance().getInfo().getPortalPosition();
        String name = UUID.randomUUID().toString();
        DataDrawData data = DataDrawData.builder().rotation(90).position(new V3(pos.getX() + 1.4, pos.getY() + 2.25, pos.getZ())).scale(1).
                dimensions(new V2(2.8, 0.5)).name(name).strings(getStrings("Board")).build();
        IRenderService.get().createGlobalWorldRenderData(world.getUID(), name, WorldRenderData.builder().dataDrawData(data).name(name).visibilityTarget(VisibilityTarget.BLACKLIST).build());
        IRenderService.get().setRenderVisible(world.getUID(), name, true);
//
//        Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), () -> {
//            data.setStrings(getStrings("lol"));
//            IRenderService.get().setRenderVisible(world.getUID(), name, false);
//            IRenderService.get().setRenderVisible(world.getUID(), name, true);
//        }, 20, 20);
    }

    private static List<StringDrawData> getStrings(String className) {
        return Arrays.asList(
                StringDrawData.builder().align(1).scale(4).string(className).position(new V2(135, 10)).build()
        );
    }

    private void loadBoard() {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective obj = scoreboard.registerNewObjective("Lobby", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Team[] teams = new Team[2];
        for (int i = 0; i < 2; i++) {
            teams[i] = scoreboard.registerNewTeam("" + i);
            final String x = "§" + (char)('a' + i);
            teams[i].addEntry(x);
            obj.getScore(x).setScore(2-i);
        }

        player.setScoreboard(scoreboard);
        scoreboardUpdater = new BukkitRunnable() {
            final PlayerInfo info = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
            @Override
            public void run() {
                setText(teams[0], Lang.get("lobby.selectedClass")
                        .replace("%class%", Lang.get("classes." + info.getSelectedClass())));
                setText(teams[1], Lang.get("lobby.moneyBalance")
                        .replace("%money%", String.valueOf(info.getBalance())));
            }
        };
    }

    private void loadPortal() {
        BukkitRunnable runnable = new BukkitRunnable() {
            double angle = 0;
            final double turnsPerSec = 0.25;
            final double radius = 1.2;
            final int parts = 3;
            final Location center = Lobby.getInstance().getInfo().getPortalPosition().toLocation(Lobby.getInstance().getWorld());
            @Override
            public void run() {
                for (int i = 0; i < parts; i++) {
                    final double cur = angle + MathUtils.PI2 * i / parts;
                    final Location at = center.clone().add(0, MathUtils.sin(cur) * radius,MathUtils.cos(cur) * radius);
                    player.spawnParticle(Particle.FLAME, at, 0, 0, 0, 0);
                }
                angle += MathUtils.PI2 * turnsPerSec / 20;
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

    public Player getPlayer() {
        return player;
    }

    public Runnable getScoreboardUpdater() {
        return scoreboardUpdater;
    }

    public List<BukkitRunnable> getRunnables() {
        return runnables;
    }
}
