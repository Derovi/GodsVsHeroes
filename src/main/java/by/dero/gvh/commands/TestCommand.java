package by.dero.gvh.commands;

import by.dero.gvh.lobby.monuments.DonatePackChest;
import by.dero.gvh.lobby.Lobby;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        Player player = (Player) commandSender;
    
        DonatePackChest.setAnimCnt(DonatePackChest.getAnimCnt() + 1);
        
        for (int i = 0; i < 5; i++) {
            player.getWorld().spawnParticle(Particle.END_ROD, Lobby.getInstance().getChest().getInCircle(i).add(0, 1, 0), 0, 0, 0, 0);
        }
        return true;
    }
}
