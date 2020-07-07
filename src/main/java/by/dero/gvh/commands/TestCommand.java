package by.dero.gvh.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {


        try {
            Player p = (Player) commandSender;
            Sound sound = Sound.values()[Integer.parseInt(strings[0])];
            Bukkit.getServer().broadcastMessage(sound.toString());
            SkeletonHorse
            p.getWorld().playSound(p.getLocation(), sound, 1.7f, 1);
        } catch (NumberFormatException ignored) {

        }
        return true;
    }
}
