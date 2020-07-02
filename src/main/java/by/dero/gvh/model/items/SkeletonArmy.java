package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.SkeletonArmyInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.PathfinderAttackEnemies;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftWitherSkeleton;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkeletonArmy extends Item implements PlayerInteractInterface, UltimateInterface {
    private final int melee;
    private final int range;

    public SkeletonArmy(String name, int level, Player owner) {
        super(name, level, owner);
        SkeletonArmyInfo info = (SkeletonArmyInfo) getInfo();
        melee = info.getMelee();
        range = info.getRange();
    }

    @Override
    public void drawSign(Location loc) {

    }

    private void initAttributes(CraftSkeleton monster) {
        EntityMonster handle = monster.getHandle();
        handle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8);
        //armor
        handle.getAttributeInstance(GenericAttributes.i).setValue(20);
        handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100);
        handle.fireProof = true;

        handle.targetSelector = new PathfinderGoalSelector(handle.world.methodProfiler);
        handle.targetSelector.a(0, new PathfinderAttackEnemies<>(
                handle, EntityLiving.class, 100, true, false, GameUtils.getTargetPredicate(getTeam())));
        handle.targetSelector.a(1, new PathfinderGoalHurtByTarget(handle, false, new Class[0]));
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        cooldown.reload();
        for (int i = 0; i < melee; i++) {
            CraftWitherSkeleton skeleton = (CraftWitherSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.WITHER_SKELETON, getTeam());
            initAttributes(skeleton);
        }
        for (int i = 0; i < range; i++) {
            CraftSkeleton skeleton = (CraftSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.SKELETON, getTeam());
            initAttributes(skeleton);
        }
    }
}
