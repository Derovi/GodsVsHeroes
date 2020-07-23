package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.cosmetic.AllCosmetic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        AllCosmetic cosmeticInterface = new AllCosmetic(Lobby.getInstance().getInterfaceManager(),
                player, "all");
        cosmeticInterface.update();
        cosmeticInterface.open();
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
            player.setExp(Float.parseFloat(args[0]));
        } catch (Exception ignored) {
        
        }
        return true;
    }
}
