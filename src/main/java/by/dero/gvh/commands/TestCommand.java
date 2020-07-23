package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.ItemDescriptionBook;
import by.dero.gvh.fireworks.FireworkSpawner;
import by.dero.gvh.model.Drawings;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        ItemDescriptionBook book = new ItemDescriptionBook(Plugin.getInstance().getBookManager(),
                player, "paladin", "swordthrow");
        book.build();
        book.open();
        //FireworkSpawner.spawn(player.getLocation(), FireworkEffect.builder().withColor(
        //        Drawings.colors[(int)(Math.random()*Drawings.colors.length)]).flicker(true).build(), player);
        //Location location = player.getLocation();
        //double yaw = Math.toRadians(location.getYaw());
        //location.add(-Math.sin(yaw) * 1.2, 0, Math.cos(yaw) * 1.2);
        //Drawings.drawFist(location, 3, Particle.FLAME);
        //BookGUI gui = new BookGUI(player);
        //gui.open();
        return true;
    }
}
