package by.dero.gvh.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player p = (Player) commandSender;
        try {
            int idx = Integer.parseInt(strings[0]);
            p.getWorld().playSound(p.getLocation(), Sound.values()[idx], 32, 20);
            p.sendMessage(Sound.values()[idx].toString());
        } catch (Exception ignored) {

        }
        return true;
    }
}
