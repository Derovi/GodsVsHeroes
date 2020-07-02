package by.dero.gvh.model.items;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.SkeletonArmyInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.PathfinderAttackEnemies;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Particle;
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

    private void initAttributes(CraftSkeleton monster, boolean isMelee) {
        EntitySkeletonAbstract handle = monster.getHandle();
        handle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8);
        //armor
        handle.getAttributeInstance(GenericAttributes.i).setValue(20);
        handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100);
        handle.fireProof = true;

        GameUtils.addTeamAi(monster, getTeam());
        handle.goalSelector.a(handle.c);
        handle.goalSelector.a(handle.b);
        if (isMelee) {
            handle.goalSelector.a(4, handle.c);
        } else {
            handle.goalSelector.a(4, handle.b);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        cooldown.reload();
        GamePlayer player = GameUtils.getPlayer(owner.getName());
        for (int i = 0; i < melee; i++) {
            CraftWitherSkeleton skeleton = (CraftWitherSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.WITHER_SKELETON, player);
            Drawings.drawCircle(skeleton.getLocation(), 2, Particle.DRAGON_BREATH);
            initAttributes(skeleton, true);
        }
        for (int i = 0; i < range; i++) {
            CraftSkeleton skeleton = (CraftSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.SKELETON, player);
            Drawings.drawCircle(skeleton.getLocation(), 2, Particle.DRAGON_BREATH);
            initAttributes(skeleton, false);
        }
    }
}
