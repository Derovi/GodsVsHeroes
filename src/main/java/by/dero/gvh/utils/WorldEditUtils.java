package by.dero.gvh.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import java.io.File;
import java.io.FileInputStream;

public class WorldEditUtils {

    public static void pasteSchematic(File file, org.bukkit.World world, Position position) {
        try {
            Vector to = new Vector(position.getX(), position.getY(), position.getZ());

            World weWorld = new BukkitWorld(world);
            WorldData worldData = weWorld.getWorldData();
            Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(file)).read(worldData);

            EditSession extent = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            AffineTransform transform = new AffineTransform();

            ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getOrigin(), extent, to);
            if (!transform.isIdentity()) copy.setTransform(transform);
            Operations.completeLegacy(copy);
            extent.flushQueue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}