package by.dero.gvh.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.world.World;
import net.minecraft.server.v1_15_R1.WorldData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEditUtils {

    public static void pasteSchematic(File file, org.bukkit.World world, Position position) {
        try {
            EditSession editSession = ClipboardFormats.findByFile(file).load(file).paste(
                    new BukkitWorld(world), new BlockVector3() {
                        @Override
                        public int getX() {
                            return (int) position.getX();
                        }

                        @Override
                        public int getY() {
                            return (int) position.getY();
                        }

                        @Override
                        public int getZ() {
                            return (int) position.getZ();
                        }
                    }, false, true, (Transform) null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}