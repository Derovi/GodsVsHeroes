package by.dero.gvh.nmcapi.dragon;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ControlledDragon {
    private DragonBase dragonBase;
    private RotatingDragon dragon;
    private EmptyArmorStand empty1;
    private EmptyArmorStand empty2;
    private Player player;

    public ControlledDragon(Player player) {
        this.player = player;
        dragonBase = new DragonBase(this);
        dragonBase.spawn();
        RotatingDragon dragon = new RotatingDragon(player.getLocation());
        dragon.spawn();
        EmptyArmorStand empty1 = new EmptyArmorStand(player.getLocation());
        EmptyArmorStand empty2 = new EmptyArmorStand(player.getLocation());
        empty1.spawn();
        empty2.spawn();
        dragon.a(dragonBase, true);
        empty1.a(dragonBase, true);
        empty2.a(empty1, true);
        ((CraftPlayer) player).getHandle().a(empty2, true);
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
