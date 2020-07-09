package by.dero.gvh.commands;

import by.dero.gvh.utils.GameUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {


        try {
            Player p = (Player) commandSender;
//            Sound sound = Sound.values()[Integer.parseInt(strings[0])];
//            Bukkit.getServer().broadcastMessage(sound.toString());
//            p.getWorld().playSound(p.getLocation(), sound, 1.07f, 1);
            GameUtils.getPlayer(p.getName()).addEffect(new PotionEffect(PotionEffectType.SPEED,
                    Integer.parseInt(args[0]), Integer.parseInt(args[1])));
        } catch (NumberFormatException ignored) {
        }
        return true;
    }
}
