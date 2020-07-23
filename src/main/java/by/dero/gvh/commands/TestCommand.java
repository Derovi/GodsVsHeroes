package by.dero.gvh.commands;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.BuyCosmeticInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        BuyCosmeticInterface obj = new BuyCosmeticInterface(Lobby.getInstance().getInterfaceManager(), player, "");
        obj.open();
        
        try {
            player.setExp(Float.parseFloat(args[0]));
        } catch (Exception ignored) {
        
        }
        return true;
    }
}
