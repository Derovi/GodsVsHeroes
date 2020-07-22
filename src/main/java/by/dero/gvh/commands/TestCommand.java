package by.dero.gvh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
//        FireworkSpawner.spawn(player.getLocation(), FireworkEffect.builder().withColor(
//                Drawings.colors[(int)(Math.random()*Drawings.colors.length)]).flicker(true).build(), player);
        //Location location = player.getLocation();
        //double yaw = Math.toRadians(location.getYaw());
        //location.add(-Math.sin(yaw) * 1.2, 0, Math.cos(yaw) * 1.2);
        //Drawings.drawFist(location, 3, Particle.FLAME);
        //BookGUI gui = new BookGUI(player);
        //gui.open();
        try {
            player.setExp(Float.parseFloat(args[0]));
        } catch (Exception ignored) {
        
        }
        return true;
    }
}
