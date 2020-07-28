package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.monuments.DonatePackChest;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TestCommand implements CommandExecutor {
    static boolean flag = false;
    
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
    
        if (args.length == 0) {
            flag = true;
            DonatePackChest.setAnimCnt(DonatePackChest.getAnimCnt() + 1);
        } else if (args.length == 1) {
            try {
                player.sendMessage(Sound.values()[Integer.parseInt(args[0])].toString() + " " + args[0]);
                player.playSound(player.getEyeLocation(), Sound.values()[Integer.parseInt(args[0])], 1, 1);
            } catch (Exception ignored) {
            
            }
        } else if (args.length == 2) {
            try {
                flag = false;
                new BukkitRunnable() {
                    int idx = Integer.parseInt(args[0]);
                    int end = Integer.parseInt(args[1]);
                    @Override
                    public void run() {
                        player.sendMessage(Sound.values()[idx].toString() + " " + idx);
                        player.playSound(player.getEyeLocation(), Sound.values()[idx], 1, 1);
                        idx++;
                        if (idx == end || flag) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Plugin.getInstance(), 20, 20);
            } catch (Exception ignored) {
        
            }
        }
        
        
        return true;
    }
}
