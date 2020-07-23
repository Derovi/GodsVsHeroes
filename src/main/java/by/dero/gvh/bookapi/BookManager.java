package by.dero.gvh.bookapi;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BookManager implements CommandExecutor {
    private final HashMap<UUID, BookGUI> players = new HashMap<>();

    public BookManager() {
        Bukkit.getPluginCommand("bookapi").setExecutor(this);
    }

    public void registerBook(Player player, BookGUI gui) {
        players.put(player.getUniqueId(), gui);
    }

    public void closeBook(Player player) {
        players.remove(player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if (!players.containsKey(player.getUniqueId())) {
            return false;
        }
        BookGUI gui = players.get(player.getUniqueId());
        if (!gui.getButtons().containsKey(UUID.fromString(args[0]))) {
            player.sendMessage("§4No such button");
            return true;
        }
        gui.getButtons().get(UUID.fromString(args[0])).onClick.run();
        return false;
    }
}
