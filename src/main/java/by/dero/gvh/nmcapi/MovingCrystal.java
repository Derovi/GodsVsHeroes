package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.dragon.EmptyArmorStand;
import by.dero.gvh.utils.Position;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

public class MovingCrystal extends EmptyArmorStand {
    private final CastratedEnderCrystal crystal;

    private double baseHeight;
    private double maxHeight = 36;
    private double progress = 0;  // from 0 to 1

    public MovingCrystal(Location loc) {
        super(loc.add(0.5, 0.5, 0.5));
        baseHeight = loc.getY();
        crystal = new CastratedEnderCrystal(loc);
        crystal.invulnerable = true;
        crystal.noclip = true;
        crystal.fireProof = true;
        crystal.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
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
