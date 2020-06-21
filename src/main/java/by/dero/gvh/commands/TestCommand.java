package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
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

        spawnUnlockParticles((Player)commandSender, 240, 1.5, Math.toRadians(-70), Math.toRadians(70));
        return true;
    }
}
