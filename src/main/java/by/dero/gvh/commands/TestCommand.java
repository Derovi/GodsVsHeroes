package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setVelocity(new Vector(0, 0.49, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setVelocity(new Vector(0, 1.4, 0));
                player.addPassenger(stand);
            }
        }.runTaskLater(Plugin.getInstance(), 10);
        System.out.println(player.getInventory().getItemInMainHand().getType());
        stand.setItemInHand(player.getInventory().getItemInMainHand());
        stand.setVisible(false);
        stand.setRightArmPose(new EulerAngle(Math.PI * 1.5, 0, 0));
        return true;
    }
}
