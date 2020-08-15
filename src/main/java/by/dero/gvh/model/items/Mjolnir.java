package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.CustomizationContext;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.Dropping;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ThrowingWeapon;
import by.dero.gvh.model.itemsinfo.MjolnirInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingMjolnir;
import by.dero.gvh.utils.GameUtils;
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

public class Mjolnir extends Item implements PlayerInteractInterface, ThrowingWeapon, Dropping {
    private final double damage;

    public Mjolnir(String name, int level, Player owner) {
        super(name, level, owner);
        final MjolnirInfo info = (MjolnirInfo) getInfo();
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
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingMjolnir mjolnir = new ThrowingMjolnir(owner, getItemStack());
    
        final int slot = owner.getInventory().getHeldItemSlot();
        owner.getInventory().removeItem(owner.getInventory().getItem(slot));
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  1.07f, 1);
        mjolnir.spawn();
        mjolnir.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(mjolnir.getHoldEntity(), getTeam())) {
                Location at = mjolnir.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at, 50,
                        new MaterialData(Material.REDSTONE_BLOCK));
                GameUtils.damage(damage, (LivingEntity) mjolnir.getHoldEntity(), owner);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    mjolnir.backToOwner();
                }
            }.runTaskLater(Plugin.getInstance(), 3);
        });
        mjolnir.setOnReturned(() -> {
            if (owner.getInventory().getItem(slot) == null ||
                    owner.getInventory().getItem(slot).getType().equals(Material.AIR)) {
                owner.getInventory().setItem(
                        slot, getInfo().getItemStack(new CustomizationContext(getOwner(), getOwnerGP().getClassName())));
                owner.getInventory().getItem(slot).setAmount(1);
            }
        });
        mjolnir.setOnHitBlock(() -> {
            new BukkitRunnable() {
                @Override
                public void run () {
                    if (mjolnir.isRemoved()) {
                        this.cancel();
                        return;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            mjolnir.backToOwner();
                        }
                    }.runTaskLater(Plugin.getInstance(), 3);
                }
            }.runTaskTimer(Plugin.getInstance(), 0, 5);
            owner.getWorld().playSound(mjolnir.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_SHULKER_BOX_OPEN, 1.07f, 1);
        });
    }
}
