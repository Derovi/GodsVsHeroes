package by.dero.gvh.commands;

import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        Location location = player.getLocation();
        double yaw = Math.toRadians(location.getYaw());
        location.add(-Math.sin(yaw) * 1.2, 0, Math.cos(yaw) * 1.2);
        Drawings.drawFist(location, 3, Particle.FLAME);
        //BookGUI gui = new BookGUI(player);
        //gui.open();
        return true;
    }
}
