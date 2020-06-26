package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.throwing.ThrowingAxe;
import by.dero.gvh.nmcapi.throwing.ThrowingItem;
import by.dero.gvh.nmcapi.throwing.ThrowingSword;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        //Location loc = player.getLocation().clone();
        //loc.setX(loc.getX() + 20);
        //EnderDragon enderDragon = (EnderDragon) player.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
        //enderDragon.addPassenger(player);
        ThrowingSword axe = new ThrowingSword(player, player.getItemInHand().getType());
       axe.spawn();
        return true;
    }
}
