package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.GameObject;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.HealPotionInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.SpawnUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        for (final GameObject ent : GameUtils.getGameObjects()) {
            if (ent.getTeam() == getTeam() && ent.getEntity().getLocation().distance(owner.getLocation()) <= radius) {
                final double hp = Math.min(ent.getEntity().getHealth() + (ent.getEntity().equals(owner) ? heal : allyHeal),
                        ent.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                ent.getEntity().setHealth(hp);
                if (ent instanceof GameMob) {
                    ((GameMob) ent).updateName();
                }
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ownerGP.isCharged(getName())) {
            owner.setCooldown(Material.SPLASH_POTION, (int) cooldown.getDuration());
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
        summonedEntityIds.add(SpawnUtils.spawnSplashPotion(owner.getEyeLocation(), 1,
                PotionType.INSTANT_HEAL, owner).getUniqueId());
    }
}
