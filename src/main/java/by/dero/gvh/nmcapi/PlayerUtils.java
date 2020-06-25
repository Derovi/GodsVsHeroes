package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class PlayerUtils {
    public static void jumpDown(Player player, int duration) {
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        prepareArmorStand(player, duration, armorStand);
    }

    public static void jumpUp(Player player, int duration) {
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        armorStand.setVelocity(new Vector(0,1,0));
        prepareArmorStand(player, duration, armorStand);
    }

    private static void prepareArmorStand(Player player, int duration, ArmorStand armorStand) {
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(false);
        armorStand.setMarker(true);
        armorStand.addPassenger(player);
        ((CraftEntity) armorStand).getHandle().noclip = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                armorStand.remove();
            }
        }.runTaskLater(Plugin.getInstance(), duration);
    }
}
