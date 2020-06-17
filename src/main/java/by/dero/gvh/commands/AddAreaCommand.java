package by.dero.gvh.commands;

import by.dero.gvh.Minigame;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.model.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class AddAreaCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 8) {
            return;
        }
        sender.sendMessage("abassis");
        World world = getPlayer(sender.getName()).getPlayer().getWorld();
        Minigame.getAreaManager().addArea(new Area(
                new Location(
                        world,
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2])
                ),
                new Location(
                        world,
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]),
                        Integer.parseInt(args[5])
                ),
                args[6].equals("1"),
                args[7].equals("1")
        ));
        Bukkit.getServer().broadcastMessage(Minigame.getAreaManager().toString());
    }

    @Override
    public String getDescription() {
        return "/game area x y z x y z territoryDamage entityDamage\n";
    }
}