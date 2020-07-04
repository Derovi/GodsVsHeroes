package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.utils.IntPosition;
import by.dero.gvh.utils.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockInfo {
    private int x;
    private int y;
    private int z;
    private Material material;

    public BlockInfo(int x, int y, int z, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }

    public Block build(World world) {
        return build(world, new IntPosition(0, 0, 0));
    }

    public Block build(World world, IntPosition relative) {
        Block block = world.getBlockAt(new Location(world, x + relative.getX(),
                y + relative.getY(),
                z + relative.getZ()));
        block.setType(material);
        return block;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
