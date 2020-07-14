package by.dero.gvh.minigame;

import by.dero.gvh.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class MapManager {
    private class BlockEntry {
        public BlockEntry(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getId() {
            return MapManager.getId(x, y, z);
        }

        private int x;
        private int y;
        private int z;

        private int expTime;

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

        public int getExpTime() {
            return expTime;
        }

        public void setExpTime(int expTime) {
            this.expTime = expTime;
        }
    }

    private class DestroyedBlockEntry extends BlockEntry {
        private final Material material;

        public DestroyedBlockEntry(int x, int y, int z, Material material) {
            super(x, y, z);
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }
    }

    private final long startTime;
    private final World world;
    private final BukkitRunnable runnable;

    MapManager(World world) {
        this.world = world;
        startTime = System.currentTimeMillis();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                while (!placedBlocksQueue.isEmpty()) {
                    BlockEntry entry = placedBlocksQueue.element();
                    if (entry.getExpTime() > currentTime) {
                        break;
                    }
                    world.getBlockAt(new Location(world, entry.x, entry.y, entry.z)).setType(Material.AIR);
                    placedBlocks.remove(entry.getId());
                    placedBlocksQueue.remove();
                }
                while (!destroyedBlocksQueue.isEmpty()) {
                    DestroyedBlockEntry entry = destroyedBlocksQueue.element();
                    if (entry.getExpTime() > currentTime) {
                        break;
                    }
                    world.getBlockAt(new Location(world, entry.getX(), entry.getY(), entry.getZ())).setType(entry.getMaterial());
                    destroyedBlocks.remove(entry.getId());
                    destroyedBlocksQueue.remove();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 20, 20);
    }

    public void finish() {
        runnable.cancel();
        while (!placedBlocksQueue.isEmpty()) {
            BlockEntry entry = placedBlocksQueue.element();
            world.getBlockAt(new Location(world, entry.x, entry.y, entry.z)).setType(Material.AIR);
            placedBlocks.remove(entry.getId());
            placedBlocksQueue.remove();
        }
        while (!destroyedBlocksQueue.isEmpty()) {
            DestroyedBlockEntry entry = destroyedBlocksQueue.element();
            world.getBlockAt(new Location(world, entry.getX(), entry.getY(), entry.getZ())).setType(entry.getMaterial());
            destroyedBlocks.remove(entry.getId());
            destroyedBlocksQueue.remove();
        }
    }

    HashSet<Integer> placedBlocks = new HashSet<>();
    Queue<BlockEntry> placedBlocksQueue = new LinkedList<>();

    HashSet<Integer> destroyedBlocks = new HashSet<>();
    Queue<DestroyedBlockEntry> destroyedBlocksQueue = new LinkedList<>();

    public static int getId(int x, int y, int z) {
        long mult = 16769023;
        long mod = 433494437;
        return (int) (((x * mult + y) % mod * mult + z) % mod);
    }

    public void blockPlaced(int x, int y, int z, int duration) {
        int id = getId(x, y, z);
        if (destroyedBlocks.contains(id)) {
            return;
        }
        BlockEntry entry = new BlockEntry(x, y, z);
        entry.setExpTime((int) (System.currentTimeMillis() - startTime) + duration);
        placedBlocks.add(id);
        placedBlocksQueue.add(entry);
    }

    public void blockDestroyed(int x, int y, int z, int duration, Material material) {
        int id = getId(x, y, z);
        if (placedBlocks.contains(id)) {
            return;
        }
        DestroyedBlockEntry entry = new DestroyedBlockEntry(x, y, z, material);
        entry.setExpTime((int) (System.currentTimeMillis() - startTime) + duration);
        destroyedBlocks.add(id);
        destroyedBlocksQueue.add(entry);
    }

    public HashSet<Integer> getPlacedBlocks() {
        return placedBlocks;
    }

    public Queue<BlockEntry> getPlacedBlocksQueue() {
        return placedBlocksQueue;
    }
}
