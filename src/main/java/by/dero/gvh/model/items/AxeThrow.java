package by.dero.gvh.model.items;

import by.dero.gvh.ChargesManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AxeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingAxe;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AxeThrow extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final Material material;
    private final double damage;
    public AxeThrow(String name, int level, Player owner) {
        super(name, level, owner);
        final AxeThrowInfo info = (AxeThrowInfo) getInfo();
        damage = info.getDamage();
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingAxe axe = new ThrowingAxe(owner, material);

        final int slot = owner.getInventory().getHeldItemSlot();
        axe.spawn();
        axe.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(axe.getHoldEntity(), getTeam())) {
                GameUtils.damage(damage, (LivingEntity) axe.getHoldEntity(), owner);
            }
        });
        axe.setOnOwnerPickUp(() -> {
            ChargesManager.getInstance().addItem(owner, this, slot);
            axe.remove();
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                axe.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration());
        Game.getInstance().getRunnables().add(runnable);
    }
}
