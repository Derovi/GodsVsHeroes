package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.interfaces.VehicleExitInterface;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnHorse extends Item implements DoubleSpaceInterface, VehicleExitInterface {

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
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(owner);
        horse.addPassenger(owner);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_HORSE_ARMOR, 1.07f, 1);
        summonedEntityIds.add(horse.getUniqueId());
    }

    @Override
    public void onPlayerUnmount (VehicleExitEvent event) {
        horse.remove();
        horse = null;
    }
}
