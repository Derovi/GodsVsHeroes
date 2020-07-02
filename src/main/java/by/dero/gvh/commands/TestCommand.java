package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.RotatingDragon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        RotatingDragon dragon = new RotatingDragon(player.getLocation());
        dragon.spawn();
        System.out.println("Duration " + 400);
        new BukkitRunnable() {
            @Override
            public void run() {
            }
        }.runTaskLater(Plugin.getInstance(), 400);
        dragon.getBukkitEntity().setVelocity(new Vector(0,1,0));
        new BukkitRunnable() {
            @Override
            public void run() {
            }
        }.runTaskLater(Plugin.getInstance(), 25);
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        ArmorStand armorStand2 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        ArmorStand armorStand3 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.addPassenger(dragon.getBukkitEntity());
        armorStand.addPassenger(armorStand2);
        armorStand2.addPassenger(armorStand3);
        armorStand3.addPassenger(player);
        return true;
    }
}
