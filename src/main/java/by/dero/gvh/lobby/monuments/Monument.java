package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.SelectorInterface;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import org.bukkit.entity.Player;

public abstract class Monument {
    private final DirectedPosition position;
    private final String className;
    private final Player owner;

    public Monument(DirectedPosition position, String className, Player owner) {
        this.position = position;
        this.className = className;
        this.owner = owner;
    }

    public abstract void load();

    public abstract void unload();

    public void onSelect(Player player) {
        if (!owner.getName().equals(player.getName())) {
            return;
        }
        SelectorInterface selectorInterface = new SelectorInterface(
                Lobby.getInstance().getInterfaceManager(), player, className);
        selectorInterface.open();
    }

    public void onUpdateSelected(Player player) {
        if (!owner.getName().equals(player.getName())) {
            return;
        }
        System.out.println("Update selected " + className + " by " + owner.getName());
    }

    public Position getPosition() {
        return position;
    }

    public String getClassName() {
        return className;
    }

    public Player getOwner() {
        return owner;
    }
}
