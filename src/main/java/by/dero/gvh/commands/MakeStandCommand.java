package by.dero.gvh.commands;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.game.StandManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MakeStandCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length != 1) {
            sender.sendMessage("§cInvalid arguments!");
            return;
        }
        if (!Plugin.getInstance().getData().getUnits().containsKey(arguments[0])) {
            sender.sendMessage("§cClass not found!");
            return;
        }
        Player p = Plugin.getInstance().getGame().getPlayers().get(sender.getName()).getPlayer();
        Location loc = p.getLocation().clone().add(p.getLocation().getDirection().multiply(2)).add(0,2.5,0);
        StandManager.getInstance().addStand(loc, arguments[0]);
    }

    @Override
    public String getDescription() {
        return "Creates class stand";
    }
}
