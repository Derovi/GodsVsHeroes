package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.interfaces.SneakInterface;
import by.dero.gvh.model.itemsinfo.SpawnHorseInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SpawnHorse extends Item implements DoubleSpaceInterface, SneakInterface {
    private final int duration;

    public SpawnHorse(String name, int level, Player owner) {
        super(name, level, owner);
        SpawnHorseInfo info = (SpawnHorseInfo) getInfo();
        duration = info.getDuration();
    }

    private SkeletonHorse horse = null;
    private UUID last = owner.getUniqueId();
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
        final UUID uuid = horse.getUniqueId();
        last = uuid;

        summonedEntityIds.add(horse.getUniqueId());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (last.equals(uuid)) {
                    onPlayerSneak();
                }
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), duration);
        Game.getInstance().getRunnables().add(runnable);
    }

    @Override
    public void onPlayerSneak() {
        if (horse != null) {
            horse.remove();
            horse = null;
        }
    }
}
