package by.dero.gvh.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import static by.dero.gvh.model.Drawings.*;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVelocity(player.getLocation().getDirection().normalize());
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        armorStand.setRightArmPose(new EulerAngle(72, 0, 0));
        return true;
    }
}
