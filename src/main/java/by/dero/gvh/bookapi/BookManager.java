package by.dero.gvh.bookapi;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.cristalix.core.event.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class BookManager implements Listener {
    private final HashMap<UUID, BookGUI> players = new HashMap<>();

    public BookManager() {
        Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
    }

    public void registerBook(Player player, BookGUI gui) {
        players.put(player.getUniqueId(), gui);
    }

    public void closeBook(Player player) {
        players.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getOriginalMessage()[0].toPlainText();
        if (message.startsWith("bookapi")) {
            if (!players.containsKey(event.getPlayer().getUniqueId())) {
                return;
            }
            String[] args = message.split(" ");
            if (args.length == 1) {
                return;
            }
            BookGUI gui = players.get(event.getPlayer().getUniqueId());
            if (!gui.getButtons().containsKey(UUID.fromString(args[1]))) {
                event.getPlayer().sendMessage("§4No such button");
                return;
            }
            gui.getButtons().get(UUID.fromString(args[1])).onClick.run();
            event.setCancelled(true);
            event.setMessage(null);
        }
    }
}
