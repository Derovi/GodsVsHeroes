package by.dero.gvh.nmcapi.dragon;

import org.bukkit.entity.Player;

public class ControlledDragon {
    private Player player;

    public ControlledDragon(Player player) {
        this.player = player;
    }

    // called from DragonBase.B_()
    public void update() {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
