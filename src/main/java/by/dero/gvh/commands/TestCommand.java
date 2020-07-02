package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.dragon.ControlledDragon;
import by.dero.gvh.nmcapi.dragon.RotatingDragon;
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
        ControlledDragon controlledDragon = new ControlledDragon(player);
        return true;
    }
}
