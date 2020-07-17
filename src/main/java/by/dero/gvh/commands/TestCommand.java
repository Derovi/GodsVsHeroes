package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.nmcapi.dragon.EmptyArmorStand;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        BookGUI gui = new BookGUI(player);
        gui.open();
        return true;
    }
}
