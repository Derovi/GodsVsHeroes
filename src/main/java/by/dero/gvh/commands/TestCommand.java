package by.dero.gvh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.dero.gvh.model.Drawings.*;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        final Player player = (Player) commandSender;
        spawnUnlockParticles(player.getLocation().clone(), player, 240,
                1.5, Math.toRadians(-70), Math.toRadians(70));
        return true;
    }
}
