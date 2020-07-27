package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.lobby.interfaces.SelectorInterface;
import by.dero.gvh.lobby.interfaces.UnlockInterface;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import org.bukkit.entity.Player;

public abstract class Monument {
    private final DirectedPosition position;
    private final String className;

    public Monument(DirectedPosition position, String className) {
        this.position = position;
        this.className = className;
    }

    public abstract void load();

    public abstract void unload();

    public void onSelect(Player player) {
        if (Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).isClassUnlocked(className)) {
            SelectorInterface selectorInterface = new SelectorInterface(
                    Lobby.getInstance().getInterfaceManager(), player, className);
            selectorInterface.open();
        }else {
            UnlockInterface unlockInterface = new UnlockInterface(
                    Lobby.getInstance().getInterfaceManager(), player, className);
            unlockInterface.open();
        }
    }

    public void onUpdateSelected(Player player) {
    }

    public Position getPosition() {
        return position;
    }

    public String getClassName() {
        return className;
    }
}
