package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.InstantFirework;
<<<<<<< HEAD
import by.dero.gvh.nmcapi.PassiveChicken;
=======
import by.dero.gvh.nmcapi.SmartFallingBlock;
>>>>>>> 26677aecc494980f88cf974caf239fa74d804de9
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCamera;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftChicken;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
<<<<<<< HEAD
import org.bukkit.scheduler.BukkitRunnable;
=======
>>>>>>> 26677aecc494980f88cf974caf239fa74d804de9
import org.bukkit.util.Vector;

import static by.dero.gvh.model.Drawings.spawnFirework;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        PassiveChicken passiveChicken = new PassiveChicken(player.getLocation());
        passiveChicken.spawn();
        //chicken.setVelocity(new Vector(0,1.5,0));
        new BukkitRunnable() {
            @Override
            public void run() {
                //chicken.setGravity(false);
                //chicken.setVelocity(new Vector(0,0,0));
                //chicken.teleport(chicken.getLocation().clone().setDirection(new Vector(0,-1,0)));
            }
        }.runTaskLater(Plugin.getInstance(), 60);
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        new BukkitRunnable() {
            @Override
            public void run() {
                passiveChicken.die();
                PacketPlayOutCamera packetPlayOutCamera = new PacketPlayOutCamera(entityPlayer);
                entityPlayer.playerConnection.sendPacket(packetPlayOutCamera);
            }
        }.runTaskLater(Plugin.getInstance(), 140);
        PacketPlayOutCamera packetPlayOutCamera = new PacketPlayOutCamera(passiveChicken);
        entityPlayer.playerConnection.sendPacket(packetPlayOutCamera);
        return true;
    }
}
