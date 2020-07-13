package by.dero.gvh.commands;

import by.dero.gvh.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdviceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§6Дать совет разработчикам: §f/advice <сообщение>");
            return true;
        }
        StringBuilder message = new StringBuilder().append(args[0]);
        for (int index = 1; index < args.length; ++index) {
            message.append(' ').append(args[index]);
        }
        Plugin.getInstance().getReportData().saveAdvice(commandSender.getName(), message.toString());
        commandSender.sendMessage("§aСпасибо, что помогаете нам улучшить §6EtherWar§f!");
        return true;
    }
}
