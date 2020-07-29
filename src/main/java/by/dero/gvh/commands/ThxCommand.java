package by.dero.gvh.commands;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class ThxCommand implements CommandExecutor {
	private static final HashSet<UUID> sent = new HashSet<>();
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		Player player = (Player) commandSender;
		if (sent.contains(player.getUniqueId()) ||
				Game.getInstance() == null ||
				!Game.getInstance().getState().equals(Game.State.GAME)) {
			return true;
		}
		sent.add(player.getUniqueId());
		GamePlayer gp = GameUtils.getPlayer(player.getName());
		HashSet<GamePlayer> players = new HashSet<>();
		for (GamePlayer owner : Game.getInstance().getBoosterTeamSpent()) {
			if (owner.getTeam() == gp.getTeam() && !owner.getPlayer().equals(player)) {
				players.add(owner);
			}
		}
		for (GamePlayer owner : Game.getInstance().getBoosterGlobalSpent()) {
			if (!owner.getPlayer().equals(player)) {
				players.add(owner);
			}
		}
		if (players.size() == 1) {
			Player owner = players.iterator().next().getPlayer();
			player.sendMessage(Lang.get("game.thxSentOne").replace("%pl%",
					MessagingUtils.getPrefixAddition(owner) + owner.getName()));
		} else if (players.size() >= 2) {
			Iterator<GamePlayer> it = players.iterator();
			Player owner = it.next().getPlayer();
			StringBuilder msg = new StringBuilder(MessagingUtils.getPrefixAddition(owner) + owner.getName());
			while (it.hasNext()) {
				owner = it.next().getPlayer();
				msg.append("§a, ").append(MessagingUtils.getPrefixAddition(owner) + owner.getName());
			}
			player.sendMessage(Lang.get("game.thxSentMany").replace("%pl%", msg.toString()));
		}
		GamePlayerStats stats = Game.getInstance().getStats().getPlayers().get(player.getName());
		stats.setExpGained(stats.getExpGained() + 20);
		
		for (GamePlayer owner : players) {
			stats = Game.getInstance().getStats().getPlayers().get(owner.getPlayer().getName());
			stats.setExpGained(stats.getExpGained() + 20);
			owner.getPlayer().sendMessage(Lang.get("game.thxReceived").replace("%pl%",
					MessagingUtils.getPrefixAddition(player) + player.getName()));
		}
		if (Game.getInstance() instanceof DisplayInteractInterface) {
			((DisplayInteractInterface) Game.getInstance()).updateDisplays();
		}
		
		return true;
	}
}
