package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SkeletonArmyInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.PathfinderAttackEnemies;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftWitherSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkeletonArmy extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final int melee;
    private final int range;
    private final int meleeDamage;
    private final int meleeHealth;
    private final int duration;

    public SkeletonArmy(String name, int level, Player owner) {
        super(name, level, owner);
        SkeletonArmyInfo info = (SkeletonArmyInfo) getInfo();
        melee = info.getMelee();
        meleeDamage = info.getMeleeDamage();
        meleeHealth = info.getMeleeHealth();
        range = info.getRange();
        duration = info.getDuration();
    }

    private void initAttributes(CraftSkeleton monster, boolean isMelee) {
        EntitySkeletonAbstract handle = monster.getHandle();
        handle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(meleeDamage);
        //armor
        handle.getAttributeInstance(GenericAttributes.i).setValue(20);
        handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100);
        handle.getAttributeInstance(GenericAttributes.maxHealth).setValue(meleeHealth);
        handle.setHealth(meleeHealth);
        handle.fireProof = true;

        handle.goalSelector = new PathfinderGoalSelector(handle.world.methodProfiler);
        handle.targetSelector = new PathfinderGoalSelector(handle.world.methodProfiler);
        handle.targetSelector.a(0, new PathfinderAttackEnemies<>(
                handle, EntityLiving.class, 100, true, false, GameUtils.getTargetPredicate(getTeam())));

        handle.goalSelector.a(1, new PathfinderGoalFloat(handle));
        handle.goalSelector.a(handle.c);
        handle.goalSelector.a(handle.b);
        if (isMelee) {
            handle.goalSelector.a(4, handle.c);
        } else {
            handle.goalSelector.a(4, handle.b);
        }
        handle.goalSelector.a(5, new PathfinderGoalRandomStrollLand(handle, 1.0D));
        handle.goalSelector.a(6, new PathfinderGoalLookAtPlayer(handle, EntityHuman.class, 8.0F));
        handle.goalSelector.a(6, new PathfinderGoalRandomLookaround(handle));
        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), handle::die, duration);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        cooldown.reload();

        for (int i = 0; i < melee; i++) {
            CraftWitherSkeleton skeleton = (CraftWitherSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.WITHER_SKELETON, ownerGP);
            Drawings.drawCircle(skeleton.getLocation(), 2, Particle.DRAGON_BREATH);
            initAttributes(skeleton, true);
            GameUtils.getMob(skeleton.getUniqueId()).updateName();
            owner.getWorld().playSound(skeleton.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.07f, 1);
        }
        for (int i = 0; i < range; i++) {
            CraftSkeleton skeleton = (CraftSkeleton) GameUtils.spawnTeamEntity(
                    MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), EntityType.SKELETON, ownerGP);
            Drawings.drawCircle(skeleton.getLocation(), 2, Particle.DRAGON_BREATH);
            owner.getWorld().playSound(skeleton.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.07f, 1);
            GameUtils.getMob(skeleton.getUniqueId()).updateName();
            initAttributes(skeleton, false);
        }
    }
}