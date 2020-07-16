package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.HealPotionInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class HealPotion extends Item implements ProjectileHitInterface,
        InfiniteReplenishInterface, PlayerInteractInterface {
    private final double radius;
    private final int heal;
    private final int allyHeal;

    public HealPotion(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final HealPotionInfo info = (HealPotionInfo)getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
        allyHeal = info.getAllyHeal();
    }

    //TODO damage
    /*
    @Override
    public ItemStack getItemStack () {
        return setItemMeta(new Potion(PotionType.INSTANT_HEAL, 1, true).toItemStack(getInfo().getAmount()), name, getInfo());
    }*/

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity at = event.getEntity();
        for (final LivingEntity ent : GameUtils.getNearby(at.getLocation(), radius)) {
            if (GameUtils.isAlly(ent, getTeam())) {
                final double hp = Math.min(ent.getHealth() + (ent.equals(owner) ? heal : allyHeal),
                        ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                ent.setHealth(hp);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
        summonedEntityIds.add(GameUtils.spawnSplashPotion(owner.getEyeLocation(), 1,
                PotionType.INSTANT_HEAL, owner).getUniqueId());
    }
}
