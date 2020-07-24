package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.utils.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class CollectorStructure {
    private static final ArrayList<Pair<Material, Vector>> base = new ArrayList<>();
    private static final ArrayList<Pair<Material, Vector> > changing = new ArrayList<>();

    private static final ArrayList<Pair<Material, Vector> > stages = new ArrayList<>();
    private static final Stack<Pair<Material, Location> > savedBlocks = new Stack<>();

    private static void addRectangle(int stX, int stY, int stZ, int eX, int eY, int eZ, Material mat,
                              ArrayList<Pair<Material, Vector> > list) {
        for (int x = stX; x <= eX; x++) {
            for (int y = stY; y <= eY; y++) {
                for (int z = stZ; z <= eZ; z++) {
                    list.add(Pair.of(mat, new Vector(x, y, z)));
                }
            }
        }
    }

    private static void initStructure() {
        base.add(Pair.of(Material.BEACON, new Vector(0, -5, 0)));
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                base.add(Pair.of(Material.IRON_BLOCK, new Vector(dx, -6, dz)));
            }
        }

        base.add(Pair.of(Material.OBSIDIAN,new Vector(-1, -1, -1)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(1, -1, 1)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(1, -1, -1)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(-1, -1, 1)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(-1, 0, 0)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(1, 0, 0)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(0, 0, -1)));
        base.add(Pair.of(Material.OBSIDIAN,new Vector(0, 0, 1)));
        ArrayList<Pair<Material, Vector> > buf = new ArrayList<>();

        //stage 1
        addRectangle(-1, 0, -3, 1, 0, -2, Material.CONCRETE_POWDER, buf);
        addRectangle(2, 0, -1, 3, 0, 1, Material.CONCRETE_POWDER, buf);
        addRectangle(-1, 0, 2, 1, 0, 3, Material.CONCRETE_POWDER, buf);
        addRectangle(-3, 0, -1, -2, 0, 1, Material.CONCRETE_POWDER, buf);
        buf.add(Pair.of(Material.CONCRETE_POWDER,new Vector(2, 0, 2)));
        buf.add(Pair.of(Material.CONCRETE_POWDER,new Vector(-2, 0, 2)));
        buf.add(Pair.of(Material.CONCRETE_POWDER,new Vector(2, 0, -2)));
        buf.add(Pair.of(Material.CONCRETE_POWDER,new Vector(-2, 0, -2)));
        Collections.shuffle(buf);
        stages.addAll(buf);
        buf.clear();

        // stage 2
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(-1, 0, -1)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(1, 0, -1)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(-1, 0, 1)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(1, 0, 1)));
        Collections.shuffle(buf);
        stages.addAll(buf);
        buf.clear();

        //stage 3
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(0, 1, -1)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(0, 1, 1)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(-1, 1, 0)));
        buf.add(Pair.of(Material.STAINED_GLASS,new Vector(1, 1, 0)));
        Collections.shuffle(buf);
        stages.addAll(buf);
        buf.clear();

        for (int y = -4; y <= 1; y++) {
            changing.add(Pair.of(Material.STAINED_GLASS, new Vector(0, y, 0)));
        }
    }

    public static void build(Location loc) {
        if (base.isEmpty()) {
            initStructure();
        }

        loc = loc.toBlockLocation();
        for (Pair<Material, Vector> obj : base) {
            changeBlock(loc.clone().add(obj.getValue()), obj.getKey());
        }
        for (Pair<Material, Vector> obj : changing) {
            changeBlock(loc.clone().add(obj.getValue()), obj.getKey());
        }
        for (Pair<Material, Vector> obj : stages) {
            changeBlock(loc.clone().add(obj.getValue()), obj.getKey());
        }

    }

    public static void changeBlock(Location at, Material to) {
        at = at.toBlockLocation();
        savedBlocks.add(Pair.of(at.getBlock().getType(), at.clone()));
        at.getBlock().setType(to);
    }

    public static void unload() {
        while (!savedBlocks.isEmpty()) {
            savedBlocks.peek().getValue().getBlock().setType(savedBlocks.peek().getKey());
            savedBlocks.pop();
        }
    }

    public static ArrayList<Pair<Material, Vector>> getChanging () {
        return changing;
    }

    public static ArrayList<Pair<Material, Vector>> getStages () {
        return stages;
    }
}
