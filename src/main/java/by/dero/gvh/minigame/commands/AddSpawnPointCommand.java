package by.dero.gvh.minigame.commands;

import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.PluginCommand;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.Position;
import by.dero.gvh.model.storages.LocalStorage;
import com.google.gson.GsonBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;

public class AddSpawnPointCommand implements PluginCommand {
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player!");
            return;
        }
        int team;
        try {
            team = Integer.parseInt(arguments[0]);
        } catch (Exception ex) {
            sender.sendMessage("§cInvalid arguments!");
            return;
        }
        Player player = (Player) sender;
        DirectedPosition[] newPositions = Arrays.copyOf(Minigame.getInstance().getGame().getInfo().getSpawnPoints()[team],
                Minigame.getInstance().getGame().getInfo().getSpawnPoints()[team].length + 1);
        newPositions[newPositions.length - 1] = new DirectedPosition(player.getLocation().getX(),
                player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getDirection());
        Minigame.getInstance().getGame().getInfo().getSpawnPoints()[team] = newPositions;
        try {
            new LocalStorage().save("game", "game",
                    new GsonBuilder().setPrettyPrinting().create().toJson(Minigame.getInstance().getGame().getInfo()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "<team> - adds current Player location to spawn points of team.";
    }
}
