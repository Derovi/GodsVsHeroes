package by.dero.gvh.utils;

import by.dero.gvh.model.StorageInterface;

import java.io.IOException;

public class DataUtils {
    public static String loadOrDefault(StorageInterface storage, String collection, String name, String defaultObject) throws IOException {
        if (!storage.exists(collection, name)) {
            storage.save(collection, name, defaultObject);
        }
        return storage.load(collection, name);
    }

    public static void setInvisibleEntity(net.minecraft.server.v1_12_R1.Entity handle) {
        handle.setCustomNameVisible(false);
        handle.setNoGravity(true);
        handle.setInvisible(true);
        handle.noclip = true;
        handle.invulnerable = true;
    }
}
