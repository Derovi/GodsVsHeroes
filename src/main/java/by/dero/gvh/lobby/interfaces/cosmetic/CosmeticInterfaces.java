package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;

import java.util.HashMap;

public class CosmeticInterfaces {
    private static final HashMap<String, Class<? extends CosmeticInterface>> interfaces = new HashMap<>();

    public static boolean exists(String name) {
        return interfaces.containsKey(name);
    }

    public static Class<? extends CosmeticInterface> get(String name) {
        return interfaces.get(name);
    }

    static {
        //interfaces.put("horseman", HorsemanCosmetic.class);
        //interfaces.put("warrior", WarriorCosmetic.class);
    }
}