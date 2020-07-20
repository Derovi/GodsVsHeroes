package by.dero.gvh.utils;

import org.bukkit.Difficulty;
import org.bukkit.World;

public class WorldUtils {
    public static void prepareWorld(World world) {
        world.setTime(1000);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRuleValue("keepInventory", "true");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobLoot", "false");
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("randomTickSpeed", "0");
    }
}
