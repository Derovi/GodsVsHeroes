package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.MovingCrystal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        MovingCrystal crystal = new MovingCrystal(player.getLocation());
        crystal.spawn();
        crystal.setMaxHeight(30);
        new BukkitRunnable() {
            double progress = 0;
            boolean up = true;

            @Override
            public void run() {
                if (up) {
                    if (progress + 0.02 > 1) {
                        up = false;
                    } else {
                        progress += 0.02;
                    }
                } else {
                    if (progress - 0.02 < 0) {
                        up = true;
                    } else {
                        progress -= 0.02;
                    }
                }
                crystal.setProgress(progress);
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
        return true;
    }
}
