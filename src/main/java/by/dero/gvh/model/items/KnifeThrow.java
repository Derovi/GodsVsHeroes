package by.dero.gvh.model.items;

import by.dero.gvh.CosmeticManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.CosmeticInfo;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.Dropping;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ThrowingWeapon;
import by.dero.gvh.model.itemsinfo.KnifeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingKnife;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class KnifeThrow extends Item implements PlayerInteractInterface, ThrowingWeapon, Dropping {
    private final Material material;
    private final double damage;

    public KnifeThrow(String name, int level, Player owner) {
        super(name, level, owner);

        KnifeThrowInfo info = (KnifeThrowInfo) getInfo();
        damage = info.getDamage();
        material = info.getMaterial();

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
        owner.setCooldown(material, (int) cooldown.getDuration());
        ItemStack itemStack = getItemStack();
        CosmeticInfo cosmeticInfo = Plugin.getInstance().getCosmeticManager().getByGroup(getOwner(),
                CosmeticManager.getWeaponGroup("assassin"));
        if (cosmeticInfo != null && cosmeticInfo.getName().equals("altairBlade")) {
            itemStack.setType(Material.DIAMOND_SWORD);
        }
        final ThrowingKnife knife = new ThrowingKnife(owner, itemStack);
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  1.07f, 1);
        knife.spawn();
        knife.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(knife.getHoldEntity(), getTeam())) {
                GameUtils.damage(damage, (LivingEntity) knife.getHoldEntity(), owner);
                Location at = knife.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at.clone().add(0,0,0), 10,
                        new MaterialData(Material.REDSTONE_BLOCK));
            }
        });
        knife.setOnHitBlock(() -> {
            Location at = knife.getItemPosition().toLocation(owner.getWorld());
            owner.getWorld().spawnParticle(Particle.CRIT_MAGIC, at, 1);
            owner.getWorld().playSound(knife.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_FENCE_GATE_CLOSE, 1.07f, 1);
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
