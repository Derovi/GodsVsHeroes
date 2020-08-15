package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.Dropping;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ThrowingWeapon;
import by.dero.gvh.model.itemsinfo.AxeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingAxe;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class AxeThrow extends Item implements PlayerInteractInterface, ThrowingWeapon, Dropping {
    private final double damage;

    public AxeThrow(String name, int level, Player owner) {
        super(name, level, owner);
        final AxeThrowInfo info = (AxeThrowInfo) getInfo();
        damage = info.getDamage();

        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0D);
        owner.saveData();
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ownerGP.getPlayerInfo().isDropWeapon()) {
            throwWeapon();
        }
    }
    
    @Override
    public void onDropItem(PlayerDropItemEvent event) {
        if (ownerGP.getPlayerInfo().isDropWeapon()) {
            throwWeapon();
        }
    }
    
    @Override
    public void throwWeapon() {
        final ThrowingAxe axe = new ThrowingAxe(owner, getItemStack());
    
        final int slot = owner.getInventory().getHeldItemSlot();
        owner.getInventory().removeItem(owner.getInventory().getItem(slot));
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  1.07f, 1);
        axe.spawn();
        axe.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(axe.getHoldEntity(), getTeam())) {
                Location at = axe.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at, 50,
                        new MaterialData(Material.REDSTONE_BLOCK));
                GameUtils.damage(damage, (LivingEntity) axe.getHoldEntity(), owner);
            }
        });
        axe.setOnHitBlock(() -> {
            new BukkitRunnable() {
                double angle = 0;
                @Override
                public void run () {
                    if (axe.isRemoved()) {
                        this.cancel();
                        return;
                    }
                    angle += Math.PI / 10;
                    for (int i = 0; i < 2; i++) {
                        double al = angle + Math.PI * i;
                        Location at = axe.getItemPosition().toLocation(owner.getWorld()).
                                add(MathUtils.cos(al)*0.5, 1, MathUtils.sin(al)*0.5);
                        owner.spawnParticle(Particle.VILLAGER_HAPPY, at, 0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(Plugin.getInstance(), 0, 5);
            owner.getWorld().playSound(axe.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_SHULKER_BOX_OPEN, 1.07f, 1);
        });
        axe.setOnOwnerPickUp(() -> {
            if (ownerGP.isInventoryHided()) {
                ownerGP.getContents()[0] = getItemStack();
            } else if (owner.getInventory().getItem(slot) == null ||
                    owner.getInventory().getItem(slot).getType().equals(Material.AIR)) {
                owner.getInventory().setItem(slot, getItemStack());
            }
            axe.remove();
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (ownerGP.isInventoryHided()) {
                    ownerGP.getContents()[0] = getItemStack();
                } else if (owner.getInventory().getItem(slot) == null ||
                        owner.getInventory().getItem(slot).getType().equals(Material.AIR)) {
                    owner.getInventory().setItem(slot, getItemStack());
                }
                axe.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration());
        Game.getInstance().getRunnables().add(runnable);
    }
}
