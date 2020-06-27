package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.throwing.GravityFireball;
import by.dero.gvh.nmcapi.throwing.ThrowingAxe;
import by.dero.gvh.nmcapi.throwing.ThrowingItem;
import by.dero.gvh.nmcapi.throwing.ThrowingSword;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSnowball;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        GravityFireball gravityFireball = new GravityFireball(player.getEyeLocation().add(new Vector(0,0.5,0)));
        Vector direction = player.getLocation().getDirection().normalize();
        //gravityFireball.setDirection(direction.getX(), direction.getY(), direction.getZ());
        gravityFireball.spawn();
        gravityFireball.setVelocity(player.getLocation().getDirection());
        gravityFireball.getFireball().addPassenger(player);
        System.out.println("spawned");
        //Fireball fireball = ((Fireball) gravityFireball.bukkitEntity);
        //fireball.setVelocity(player.getLocation().getDirection().normalize());
        //fireball.addPassenger(player);
        return true;
    }
}
