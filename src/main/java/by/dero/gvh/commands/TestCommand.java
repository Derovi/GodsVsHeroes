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

import static by.dero.gvh.model.Drawings.spawnFirework;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        SmartFallingBlock smartFallingBlock = new SmartFallingBlock(player.getLocation(), Material.WEB);
        smartFallingBlock.setVelocity(player.getLocation().getDirection());
        smartFallingBlock.spawn();
        smartFallingBlock.setOnHitBlock((Block block) -> {
            System.out.println("Hit block!");
            smartFallingBlock.setStopped(true);
            smartFallingBlock.dieLater(40);
        });
        smartFallingBlock.setOnHitEntity((Entity entity) -> {
            System.out.println("Hit entity!");
            smartFallingBlock.setHoldEntity(entity);
            smartFallingBlock.dieLater(100);
        });
        return true;
    }
}
