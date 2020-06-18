package by.dero.gvh;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager implements CommandExecutor {
    private final HashMap<String, PluginCommand> commands = new HashMap<>();

    public HashMap<String, PluginCommand> getCommands() {
        return commands;
    }

    public CommandManager() {
        Objects.requireNonNull(Bukkit.getPluginCommand("game")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandString, String[] params) {
        if (params.length == 0) {
            commandSender.sendMessage("§6====== Gods vs Heroes ======");
            for (Map.Entry<String, PluginCommand> entry : commands.entrySet()) {
                commandSender.sendMessage("§2" + entry.getKey() + "§9 " + entry.getValue().getDescription());
            }
            commandSender.sendMessage("§6============================");
            return true;
        }
        String commandName = params[0];
        if (!commands.containsKey(commandName)) {
            commandSender.sendMessage("§cCommand not found! Print /game to see help.");
            return true;
        }
        String[] arguments = new String[params.length - 1];
        System.arraycopy(params, 1, arguments, 0, params.length - 1);
        commands.get(commandName).execute(commandSender, arguments);
        return true;
    }
}
