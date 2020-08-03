package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import by.dero.gvh.utils.BridgeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToCHCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        BridgeUtils.redirectPlayer(player, "TEST-7");
        return true;
    }
}
