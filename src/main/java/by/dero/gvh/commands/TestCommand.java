package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.InstantFirework;
import by.dero.gvh.nmcapi.SmartFallingBlock;
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import static by.dero.gvh.model.Drawings.spawnFirework;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        SmartFallingBlock smartFallingBlock = new SmartFallingBlock(player.getLocation().add(0,1,0), Material.WEB);
        smartFallingBlock.setVelocity(player.getLocation().getDirection());
        smartFallingBlock.spawn();
        smartFallingBlock.setOwner(player);
        smartFallingBlock.setOnHitGround(() -> {
            System.out.println("Hit block!");
            smartFallingBlock.setStopped(true);
            smartFallingBlock.dieLater(100);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
        });
        smartFallingBlock.setOnHitEntity((Entity entity) -> {
            System.out.println("Hit entity!");
            smartFallingBlock.setHoldEntity(entity);
            smartFallingBlock.dieLater(100);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
        });
        return true;
    }
}
