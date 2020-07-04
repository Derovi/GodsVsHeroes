package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.minigame.Minigame;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectorStructure {
    private static List<BlockInfo> base = null;
    private static List<List<BlockInfo>> stages = null;
    private final EtherCollector collector;

    public enum TeamColor {
        NEUTRAL, GREEN, RED, BLUE
    }

    public CollectorStructure(EtherCollector collector) {
        this.collector = collector;
        if (base == null) {
            initStructure();
        }
    }

    private static void initStructure() {
        // init base
        //base = new ArrayList<>();
        //base.add(new BlockInfo(1,0,0, Material.));
    }

    public static void changeBlockAccordingToColor(Block block, TeamColor color) {
        //  TODO
    }

    public void build(List<BlockInfo> info, TeamColor color) {
        for (BlockInfo blockInfo : info) {
            Block block = blockInfo.build(Minigame.getInstance().getWorld(), collector.getPosition());
            changeBlockAccordingToColor(block, color);
        }
    }

    public void buildStructure() {
        build(base, TeamColor.NEUTRAL);
        for (List<BlockInfo> stage : stages) {
            build(stage, TeamColor.NEUTRAL);
        }
    }

    public void buildStage(int stage, TeamColor teamColor) {
        build(stages.get(stage), teamColor);
    }

    public EtherCollector getCollector() {
        return collector;
    }

    public static int getStageCount() {
        return stages.size();
    }
}
