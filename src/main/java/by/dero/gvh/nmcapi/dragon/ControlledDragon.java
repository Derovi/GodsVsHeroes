package by.dero.gvh.nmcapi.dragon;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ControlledDragon {
    private final DragonBase dragonBase;
    private final RotatingDragon dragon;
    private final EmptyArmorStand empty1;
    private final EmptyArmorStand empty2;
    private Player player;

    public ControlledDragon(Player player) {
        this.player = player;
        dragonBase = new DragonBase(this);
        dragonBase.spawn();
        dragon = new RotatingDragon(player.getLocation());
        dragon.spawn();
        empty1 = new EmptyArmorStand(player.getLocation());
        empty2 = new EmptyArmorStand(player.getLocation());
        empty1.spawn();
        empty2.spawn();
        dragon.a(dragonBase, true);
        empty1.a(dragonBase, true);
        empty2.a(empty1, true);
        ((CraftPlayer) player).getHandle().a(empty2, true);
    }

    // called from DragonBase.B_()
    public void update() {
        if (player.getVehicle() == null || !player.getVehicle().getUniqueId().equals(empty2.getUniqueID())) {
            finish();
            return;
        }
        float pitch = player.getLocation().getPitch();
        if (player.getLocation().getPitch() < 0.0f) {
            pitch = 0.0f;
        }
        else if (player.getLocation().getPitch() > 60.0f) {
            pitch = 60.0f;
        }
        final Vector vec = player.getLocation().getDirection().multiply(1).setY(pitch / 40.0f * -1.0f + 0.75);
        move(vec.getX(), vec.getY(), vec.getZ());

        dragon.yaw = player.getLocation().getYaw() - 180.0f;
        empty2.yaw = player.getLocation().getYaw();
    }

    public void move(double dx, double dy, double dz) {
        dragonBase.locX += dx;
        dragonBase.locY += dy;
        dragonBase.locZ += dz;
    }

    public void finish() {
        empty2.die();
        empty1.die();
        dragon.die();
        dragonBase.die();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
