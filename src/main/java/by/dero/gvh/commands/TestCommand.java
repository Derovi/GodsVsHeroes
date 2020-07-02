package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.ChickenAvatar;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCamera;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        ChickenAvatar passiveChicken = new ChickenAvatar(player);
        passiveChicken.spawn();
        System.out.println("Duration " + 400);
        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Killed");
                passiveChicken.die();
            }
        }.runTaskLater(Plugin.getInstance(), 400);
        passiveChicken.setSpeed(0.06);
        passiveChicken.getBukkitEntity().setVelocity(new Vector(0,1,0));
        new BukkitRunnable() {
            @Override
            public void run() {
                passiveChicken.setSpeed(0.5);
            }
        }.runTaskLater(Plugin.getInstance(), 25);
        return true;
    }
}
