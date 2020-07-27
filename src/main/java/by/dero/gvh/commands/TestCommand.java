package by.dero.gvh.commands;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.ConfirmationInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
    
        ConfirmationInterface inter = new ConfirmationInterface(Lobby.getInstance().getInterfaceManager(), player,
                "Hui", () -> player.sendMessage("abort eto grex"), () -> player.sendMessage("a eto ne grex"));
        
        inter.open();
        
        return true;
    }
}
