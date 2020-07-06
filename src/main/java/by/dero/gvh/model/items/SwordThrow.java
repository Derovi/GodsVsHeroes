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
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
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
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  24, 1);
        sword.spawn();
        sword.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(sword.getHoldEntity(), getTeam())) {
                GameUtils.damage(damage, (LivingEntity) sword.getHoldEntity(), owner);
                Location at = sword.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at.clone().add(0,0,0), 20,
                        new MaterialData(Material.REDSTONE_BLOCK));
            }
        });
        sword.setOnHitBlock(() -> {
            new BukkitRunnable() {
                double angle = 0;
                @Override
                public void run () {
                    if (sword.isRemoved()) {
                        this.cancel();
                        return;
                    }
                    angle += Math.PI / 30;
                    for (int i = 0; i < 2; i++) {
                        double al = angle + Math.PI * i;
                        Location at = sword.getItemPosition().toLocation(owner.getWorld()).
                                add(MathUtils.cos(al)*0.5, 1, MathUtils.sin(al)*0.5);
                        owner.spawnParticle(Particle.VILLAGER_HAPPY, at, 0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(Plugin.getInstance(), 0, 2);
            owner.getWorld().playSound(sword.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_SHULKER_BOX_OPEN, 24, 1);
        });
        sword.setOnOwnerPickUp(() -> {
            ChargesManager.getInstance().addItem(GameUtils.getPlayer(owner.getName()), this, slot);
            sword.remove();
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                sword.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration()-1);
        Game.getInstance().getRunnables().add(runnable);
    }
}
