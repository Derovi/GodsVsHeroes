package by.dero.gvh.model.items;

import by.dero.gvh.Cooldown;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InteractAnyItem;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.DragonFlyInfo;
import by.dero.gvh.nmcapi.DFireball;
import by.dero.gvh.nmcapi.dragon.ControlledDragon;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonFly extends Item implements PlayerInteractInterface, InteractAnyItem {
    private final DragonFlyInfo info;
    private final Cooldown fireballCooldown;
    private ControlledDragon dragon;
    private final Material material;

    public DragonFly(String name, int level, Player owner) {
        super(name, level, owner);
        info = (DragonFlyInfo) getInfo();
        fireballCooldown = new Cooldown(info.getFireballCoolDown());
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 100, 1);
        dragon = new ControlledDragon(owner);
        dragon.setSpeed(info.getSpeed());
        new BukkitRunnable() {
            @Override
            public void run() {
                dragon.finish();
            }
        }.runTaskLater(Plugin.getInstance(), info.getDuration());
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack result = super.getItemStack();
        if (result.getType().equals(Material.SKULL_ITEM)) {
            result.setDurability((short) 5);
        }
        return result;
    }

    @Override
    public boolean playerInteract() {
        if (dragon != null && dragon.getDragon() != null && !dragon.getDragon().dead) {
            if (!fireballCooldown.isReady()) {
                return true;
            }
            fireballCooldown.reload();
            Vector vector = new Vector(owner.getLocation().getDirection().getX(), 0,
                    owner.getLocation().getDirection().getZ()).normalize().multiply(4);
            DFireball fireball = new DFireball(owner.getEyeLocation().add(vector));
            fireball.setOwner(ownerGP);
            fireball.setDirection(owner.getLocation().getDirection().getX(),
                    owner.getLocation().getDirection().getY(),
                    owner.getLocation().getDirection().getZ());
            fireball.setExplodeDamage(info.getDamage());
            fireball.spawn();
            return true;
        }
        return false;
    }
}
