package by.dero.gvh.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.cristalix.core.formatting.Colors;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
//        AllCosmetic cosmeticInterface = new AllCosmetic(Lobby.getInstance().getInterfaceManager(),
//                player, "all");
//        cosmeticInterface.update();
//        cosmeticInterface.open();
        /*ItemDescriptionBook book = new ItemDescriptionBook(Plugin.getInstance().getBookManager(),
                player, "paladin", "swordthrow");
        book.build();
        book.open();*/
        //FireworkSpawner.spawn(player.getLocation(), FireworkEffect.builder().withColor(
        //        Drawings.colors[(int)(Math.random()*Drawings.colors.length)]).flicker(true).build(), player);
//        FireworkSpawner.spawn(player.getLocation(), FireworkEffect.builder().withColor(
//                Drawings.colors[(int)(Math.random()*Drawings.colors.length)]).flicker(true).build(), player);
        //Location location = player.getLocation();
        //double yaw = Math.toRadians(location.getYaw());
        //location.add(-Math.sin(yaw) * 1.2, 0, Math.cos(yaw) * 1.2);
        //Drawings.drawFist(location, 3, Particle.FLAME);
        //BookGUI gui = new BookGUI(player);
        //gui.open();
        try {
            Bukkit.getServer().broadcastMessage(Colors.custom(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2])
            ) + "zxc");
        } catch (Exception ignored) {
        
        }
        return true;
    }
}
