package by.dero.gvh.bookapi;

import by.dero.gvh.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
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
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class BookManager implements CommandExecutor {
    private final HashMap<UUID, BookGUI> players = new HashMap<>();

    public BookManager() {
        Bukkit.getPluginCommand("bookapi").setExecutor(this);
        Bukkit.getLogger().setFilter((Filter) record -> {
            System.out.println("mes: " + record.getMessage());
            return !record.getMessage().contains("/bookapi");
        });
        Logger coreLogger = (Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new Log4JFilter());
    }

    public void registerBook(Player player, BookGUI gui) {
        players.put(player.getUniqueId(), gui);
    }

    public void closeBook(Player player) {
        players.remove(player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length != 1) {
            return true;
        }
        Player player = (Player) commandSender;
        if (!players.containsKey(player.getUniqueId())) {
            return true;
        }
        BookGUI gui = players.get(player.getUniqueId());
        if (!gui.getButtons().containsKey(UUID.fromString(args[0]))) {
            player.sendMessage("§4No such button");
            return true;
        }
        gui.getButtons().get(UUID.fromString(args[0])).onClick.run();
        return true;
    }

    //    @EventHandler(priority = EventPriority.MONITOR)
//    public void onChat(AsyncPlayerChatEvent event) {
//        String message = event.getOriginalMessage()[0].toPlainText();
//        if (message.startsWith("bookapi")) {
//            if (!players.containsKey(event.getPlayer().getUniqueId())) {
//                return;
//            }
//            String[] args = message.split(" ");
//            if (args.length == 1) {
//                return;
//            }
//            BookGUI gui = players.get(event.getPlayer().getUniqueId());
//            if (!gui.getButtons().containsKey(UUID.fromString(args[1]))) {
//                event.getPlayer().sendMessage("§4No such button");
//                return;
//            }
//            gui.getButtons().get(UUID.fromString(args[1])).onClick.run();
//            event.setCancelled(true);
//            event.setMessage(null);
//        }
//    }
}
