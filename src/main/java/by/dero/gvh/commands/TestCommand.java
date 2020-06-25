package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.PlayerUtils;
import by.dero.gvh.nmcapi.SmartArmorStand;
import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import static by.dero.gvh.model.Drawings.*;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        if (strings.length == 0) {
            PlayerUtils.jumpDown(player, 20);
        } else {
            PlayerUtils.jumpUp(player, 20);
        }
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

        /*SmartArmorStand smartArmorStand = new SmartArmorStand(player.getLocation());
        smartArmorStand.noclip = true;
        smartArmorStand.getBukkitEntity().setVelocity(player.getLocation().getDirection());
        ((CraftWorld) player.getWorld()).getHandle().addEntity(smartArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);*/
        //smartArmorStand.boundingBox = new AxisAlignedBB(3,3,3,4,4,4);
        return true;
    }
}
