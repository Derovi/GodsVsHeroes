package by.dero.gvh.nmcapi;

import by.dero.gvh.nmcapi.dragon.EmptyArmorStand;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MovingCrystal extends EmptyArmorStand {
    private final CastratedEnderCrystal crystal;

    private double baseHeight;
    private double maxHeight = 36;
    private double progress = 0;  // from 0 to 1

    public MovingCrystal(Location loc) {
        super(loc);
        baseHeight = loc.getY();
        crystal = new CastratedEnderCrystal(loc);
        crystal.spawn();
        crystal.a(this, true);
    }

    @Override
    public void B_() {
        locY = baseHeight + progress * maxHeight;
    }

    @Override
    public void die() {
        crystal.die();
        super.die();
    }

    public void setPosition(Position position) {
        setPosition(position.getX(), position.getY(), position.getZ());
        baseHeight = position.getY();
    }

    public double getBaseHeight() {
        return baseHeight;
    }

    public void setBaseHeight(double baseHeight) {
        this.baseHeight = baseHeight;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
