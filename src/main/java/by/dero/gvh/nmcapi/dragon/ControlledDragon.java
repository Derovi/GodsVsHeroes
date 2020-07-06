package by.dero.gvh.nmcapi.dragon;

import by.dero.gvh.utils.GameUtils;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ControlledDragon {
    private final DragonBase dragonBase;
    private final RotatingDragon dragon;
    private final EmptyArmorStand empty1;
    private final EmptyArmorStand empty2;
    private double speed = 1.0;
    private Player player;
    private final double sizeX = 1;
    private final double sizeY = 1;

    public ControlledDragon(Player player) {
        this.player = player;
        dragonBase = new DragonBase(this);
        dragonBase.spawn();
        dragon = new RotatingDragon(this);
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
        final Vector vec = player.getLocation().getDirection().setY(pitch / 40.0f * -1.0f + 0.75).multiply(speed);
        move(vec.getX() + dragonBase.motX, vec.getY() + dragonBase.motY, vec.getZ() + dragonBase.motZ);
        dragonBase.motY = Math.max(0, dragonBase.motY - 0.03999999910593033D);
        dragonBase.motX *= 0.9800000190734863D;
        dragonBase.motY *= 0.9800000190734863D;
        dragonBase.motZ *= 0.9800000190734863D;
        dragon.yaw = player.getLocation().getYaw() - 180.0f;
        empty2.yaw = player.getLocation().getYaw();
    }

    public void move(double dx, double dy, double dz) {
        if (!isCollideBlocks(empty2.locX + dx, empty2.locY + dy, empty2.locZ + dz)) {
            dragonBase.locX += dx;
            dragonBase.locY += dy;
            dragonBase.locZ += dz;
        }
    }

    private boolean isCollideBlocks(double dragonX, double dragonY, double dragonZ) {
        World world = player.getWorld();
        for (double x = dragonX - sizeX; x <= dragonX + sizeX; ++x) {
            for (double y = dragonY; y <= dragonY + sizeY; ++y) {
                for (double z = dragonZ - sizeX; z <= dragonZ + sizeX; ++z) {
                    if (!GameUtils.isVoid(world.getBlockAt((int) x, (int) y, (int) z).getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
