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


        return true;
    }
}
