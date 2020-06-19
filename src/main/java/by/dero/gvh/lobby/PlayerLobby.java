package by.dero.gvh.lobby;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.monuments.Monument;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import by.dero.gvh.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerLobby {
    private final LobbyRecord record;
    private final Player player;
    private final HashMap<String, Monument> monuments = new HashMap<>();
    private FlyingText selectedClass;

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
        return transformToLobbyCord(new Position(player.getLocation())).
                distance(Lobby.getInstance().getInfo().getPortalPosition()) < 2;
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
                new Position(recPos.getX() + 15.5, recPos.getY()+1, recPos.getZ()+26.5),
                "", player);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                selectedClass.setText("§aSelected class: " +
                        Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo().getSelectedClass());
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 20);
        runnables.add(runnable);
    }

    private void loadBoard() {
        Board board = new Board("Lobby", 2);
        player.setScoreboard(board.getScoreboard());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerInfo info = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
                String[] ar = new String[] {
                        ChatColor.AQUA + "Selected: " + info.getSelectedClass(),
                        ChatColor.GOLD + "Money " + info.getBalance()
                };
                board.update(ar);
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 20);
        runnables.add(runnable);
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

    public HashMap<String, Monument> getMonuments() {
        return monuments;
    }

    public List<BukkitRunnable> getRunnables() {
        return runnables;
    }
}
