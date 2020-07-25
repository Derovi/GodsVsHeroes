package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SkeletonArmyInfo;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SkeletonArmy extends Item implements PlayerInteractInterface {
    private final int melee;
    private final int range;
    private final int meleeDamage;
    private final int meleeHealth;
    private final int duration;
    private final Material material;

    public SkeletonArmy(String name, int level, Player owner) {
        super(name, level, owner);
        SkeletonArmyInfo info = (SkeletonArmyInfo) getInfo();
        melee = info.getMelee();
        meleeDamage = info.getMeleeDamage();
        meleeHealth = info.getMeleeHealth();
        range = info.getRange();
        duration = info.getDuration();
        material = info.getMaterial();
    }
    
    @Override
    public ItemStack getItemStack() {
        ItemStack result = super.getItemStack();
        if (result.getType().equals(Material.SKULL_ITEM)) {
            result.setDurability((short) 1);
        }
        return result;
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
    
        for (int i = 0; i < melee; i++) {
            GameMob gm = SpawnUtils.spawnAISkeleton(MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), meleeHealth,
                    meleeDamage, true, duration, ownerGP);
            Drawings.drawCircle(gm.getEntity().getLocation(), 2, Particle.DRAGON_BREATH);
            owner.getWorld().playSound(gm.getEntity().getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.07f, 1);
        }
        for (int i = 0; i < range; i++) {
            GameMob gm = SpawnUtils.spawnAISkeleton(MathUtils.getGoodInCylinder(owner.getLocation(), 0, 10), meleeHealth,
                    meleeDamage, false, duration, ownerGP);
            Drawings.drawCircle(gm.getEntity().getLocation(), 2, Particle.DRAGON_BREATH);
            owner.getWorld().playSound(gm.getEntity().getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.07f, 1);
        }
    }
}