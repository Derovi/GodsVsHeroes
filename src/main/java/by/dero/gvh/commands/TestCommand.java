package by.dero.gvh.commands;

import by.dero.gvh.nmcapi.InstantFirework;
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;

import static by.dero.gvh.model.Drawings.spawnFirework;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        InstantFirework correctFirework = new InstantFirework(player.getLocation().clone().add(0,3,0));
        FireworkMeta fwm = correctFirework.getMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
        correctFirework.setMeta(fwm);
        correctFirework.spawn();

        spawnFirework(player.getLocation().clone().add(-3, 3, 0), 1);
//        GravityFireball gravityFireball = new GravityFireball(player.getLocation().clone().add(0, -1,0));
//        gravityFireball.addPassenger(player);
//        gravityFireball.setVelocity(player.getLocation().getDirection().normalize().multiply(1.3));
//        gravityFireball.spawn();
        return true;
    }
}
