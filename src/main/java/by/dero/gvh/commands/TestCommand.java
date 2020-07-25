package by.dero.gvh.commands;

import by.dero.gvh.minigame.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {

        try {
            Bukkit.getServer().broadcastMessage(Game.getInstance().getStats().getDate());
        } catch (Exception ignored) {
        
        }
        return true;
    }
}
