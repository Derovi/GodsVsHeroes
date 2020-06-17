package by.dero.gvh.lobby.monuments;

import by.dero.gvh.utils.Position;
import org.bukkit.entity.Player;

public abstract class Monument {
    private final Position position;
    private final String className;
    private final Player owner;

    public Monument(Position position, String className, Player owner) {
        this.position = position;
        this.className = className;
        this.owner = owner;
    }

    abstract void load();

    abstract void unload();

    public void onSelect(Player player) {
        if (!owner.getName().equals(player.getName())) {
            return;
        }
        System.out.println("Selected " + className + " by " + owner.getName());
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
