package by.dero.gvh.commands;

import by.dero.gvh.lobby.monuments.DonatePackChest;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.nmcapi.SmartFallingBlock;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        player.getLocation().getWorld().spawnFallingBlock(
                player.getLocation(), Material.BEDROCK, (byte) 0);
        return true;
    }
}
