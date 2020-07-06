package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.KnifeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingKnife;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class KnifeThrow extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final KnifeThrowInfo info;

    public KnifeThrow(String name, int level, Player owner) {
        super(name, level, owner);
        info = (KnifeThrowInfo) getInfo();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final int slot = owner.getInventory().getHeldItemSlot();
        final ThrowingKnife knife = new ThrowingKnife(owner, info.getMaterial());
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  24, 1);
        knife.spawn();
        knife.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(knife.getHoldEntity(), getTeam())) {
                GameUtils.damage(info.getDamage(), (LivingEntity) knife.getHoldEntity(), owner);
                Location at = knife.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at.clone().add(0,0,0), 10,
                        new MaterialData(Material.REDSTONE_BLOCK));
            }
        });
        knife.setOnHitBlock(() -> {
            Location at = knife.getItemPosition().toLocation(owner.getWorld());
            owner.getWorld().spawnParticle(Particle.CRIT_MAGIC, at, 1);
            owner.getWorld().playSound(knife.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_FENCE_GATE_CLOSE, 24, 1);
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                knife.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration()-1);
        Game.getInstance().getRunnables().add(runnable);
    }
}
