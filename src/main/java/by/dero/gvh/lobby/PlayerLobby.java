package by.dero.gvh.lobby;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.commands.TestCommand;
import by.dero.gvh.lobby.monuments.Monument;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.WorldEditUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static by.dero.gvh.utils.Board.setText;

public class PlayerLobby {
    private final LobbyRecord record;
    private final Player player;
    private final HashMap<String, Monument> monuments = new HashMap<>();
    private FlyingText selectedClass;
    private Runnable scoreboardUpdater;

    private final List<BukkitRunnable> runnables = new ArrayList<>();

    public PlayerLobby(LobbyRecord record) {
        this.record = record;
        this.player = Bukkit.getPlayer(record.getOwner());
    }

    public void create() {
        WorldEditUtils.pasteSchematic(Lobby.getInstance().getLobbySchematicFile(),
                Lobby.getInstance().getWorld(), record.getPosition());

    }

    public void destroy() {

    }

    public boolean isInPortal() {
        final Position pos = transformToLobbyCord(new Position(player.getLocation()));
        final Position portal = Lobby.getInstance().getInfo().getPortalPosition();
        return pos.distance(portal) < 2 &&
                Math.abs(pos.getZ() - portal.getZ()) < 1;
    }

    public void load() {
        loadPortal();
        loadBoard();
        loadSelectedClass();

        for (Map.Entry<String, DirectedPosition> entry :
                Lobby.getInstance().getInfo().getClassNameToMonumentPosition().entrySet()) {
            try {
                String monumentName = entry.getKey();
                Monument monument = Lobby.getInstance().getMonumentManager().getClassNameToMonument().
                        get(monumentName).getConstructor(DirectedPosition.class, String.class, Player.class).
                        newInstance(transformFromLobbyCord(entry.getValue()), monumentName, player);
                monument.load();
                monuments.put(monumentName, monument);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadSelectedClass() {
        final Position recPos = record.getPosition();
        selectedClass = new FlyingText(
                new Location(getPlayer().getWorld(),recPos.getX() + 15.5, recPos.getY()+1, recPos.getZ()+26.5), "");
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
        final Position recPos = record.getPosition();
        BukkitRunnable runnable = new BukkitRunnable() {
            double angle = 0;
            final double turnsPerSec = 0.25;
            final double radius = 1.2;
            final int parts = 3;
            final Location center = recPos.toLocation(Lobby.getInstance().getWorld()).clone().add(15.5,1.5,29.5);
            @Override
            public void run() {
                for (int i = 0; i < parts; i++) {
                    final double cur = angle + Math.PI * 2 * i / parts;
                    final Location at = center.clone().add(Math.cos(cur) * radius, Math.sin(cur) * radius,0);
                    player.spawnParticle(Particle.FLAME, at, 0, 0, 0, 0);
                }
                angle += Math.PI * turnsPerSec / 20 * 2;
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);
        runnables.add(runnable);
    }

    public void unload() {
        for (BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        selectedClass.unload();
        for (Monument monument : monuments.values()) {
            monument.unload();
        }
    }

    public LobbyRecord getRecord() {
        return record;
    }

    public Player getPlayer() {
        return player;
    }

    public Position transformToLobbyCord(Position position) {
        return new Position(position.getX() - record.getPosition().getX(),
                position.getY() - record.getPosition().getY(),
                position.getZ() - record.getPosition().getZ());
    }

    public DirectedPosition transformToLobbyCord(DirectedPosition position) {
        return new DirectedPosition(position.getX() - record.getPosition().getX(),
                position.getY() - record.getPosition().getY(),
                position.getZ() - record.getPosition().getZ(),
                   position.getDirection());
    }

    public DirectedPosition transformFromLobbyCord(DirectedPosition position) {
        return new DirectedPosition(position.getX() + record.getPosition().getX(),
                position.getY() + record.getPosition().getY(),
                position.getZ() + record.getPosition().getZ(),
                   position.getDirection());
    }

    public Runnable getScoreboardUpdater() {
        return scoreboardUpdater;
    }

    public FlyingText getSelectedClass() {
        return selectedClass;
    }

    public HashMap<String, Monument> getMonuments() {
        return monuments;
    }

    public List<BukkitRunnable> getRunnables() {
        return runnables;
    }
}
