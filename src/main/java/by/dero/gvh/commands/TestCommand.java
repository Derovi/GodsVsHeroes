package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.throwing.ThrowingKnife;
import by.dero.gvh.nmcapi.throwing.ThrowingSword;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player p = (Player) commandSender;
        ThrowingKnife sword = new ThrowingKnife(p, Material.DIAMOND_SWORD);
        sword.spawn();
        return true;
    }
}
