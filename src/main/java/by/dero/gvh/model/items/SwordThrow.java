package by.dero.gvh.model.items;

import by.dero.gvh.ChargesManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.SwordThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingSword;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SwordThrow extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final Material material;
    private final double damage;
    public SwordThrow(String name, int level, Player owner) {
        super(name, level, owner);
        final SwordThrowInfo info = (SwordThrowInfo) getInfo();
        damage = info.getDamage();
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingSword sword = new ThrowingSword(owner, material);

        final int slot = owner.getInventory().getHeldItemSlot();
        sword.spawn();
        sword.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(sword.getHoldEntity(), getTeam())) {
                GameUtils.damage(damage, (LivingEntity) sword.getHoldEntity(), owner);
            }
        });
        sword.setOnOwnerPickUp(() -> {
            ChargesManager.getInstance().addItem(owner, this, slot);
            sword.remove();
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                sword.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration());
        Game.getInstance().getRunnables().add(runnable);
    }
}
