package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.ThrowingItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        /*ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVelocity(player.getLocation().getDirection().normalize());
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        armorStand.setRightArmPose(new EulerAngle(270, 0, 0));
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(false);
        armorStand.setMarker(true);
        armorStand.setCustomName("#falling_block");

        ((CraftEntity) armorStand).getHandle().noclip = true;*/

        /*ThrowingItem smartArmorStand = new ThrowingItem(player.getLocation());
        smartArmorStand.noclip = true;

        armorStand.setVelocity(player.getLocation().getDirection().normalize());
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        armorStand.setRightArmPose(new EulerAngle(270, 0, 0));
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(false);
        armorStand.setMarker(true);*/

        //((CraftWorld) player.getWorld()).getHandle().addEntity(smartArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        //smartArmorStand.boundingBox = new AxisAlignedBB(3,3,3,4,4,4);
        ThrowingItem throwingItem = new ThrowingItem(player.getLocation(), Material.DIAMOND_SWORD);
        throwingItem.setVelocity(player.getLocation().getDirection().normalize());
        throwingItem.spawn();
        return true;
    }
}
