package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.interfaces.SneakInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;

public class SpawnHorse extends Item implements DoubleSpaceInterface, SneakInterface {

    public SpawnHorse(String name, int level, Player owner) {
        super(name, level, owner);
    }

    private SkeletonHorse horse = null;

    @Override
    public void onDoubleSpace() {
        if (!cooldown.isReady() || horse != null) {
            return;
        }
        cooldown.reload();
        horse = (SkeletonHorse) GameUtils.spawnEntity(owner.getLocation(), EntityType.SKELETON_HORSE);
        horse.setAdult();
        horse.setInvulnerable(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(owner);
        horse.addPassenger(owner);

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 16, 1);
        summonedEntityIds.add(horse.getUniqueId());
    }

    @Override
    public void onPlayerSneak() {
        if (horse != null) {
            horse.remove();
            horse = null;
        }
    }
}
