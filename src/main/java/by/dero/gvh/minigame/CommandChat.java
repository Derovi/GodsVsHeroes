package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CommandChat implements Listener {
	private String getMessageText(Player player, String text, boolean global) {
		if (!global) {
			return Lang.get("commandChat.formatMessage").
					replace("%sender%", player.getDisplayName()).
					replace("%msg%", text.substring(1));
		} else {
			return text;
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		String msg = event.getMessage();
		if (msg.isEmpty()) {
			return;
		}
		Player sender = event.getPlayer();
		if (!msg.startsWith("!") || !(Plugin.getInstance().getPluginMode() instanceof Minigame)) {
			msg = getMessageText(sender, msg, true);
			for (Player ot : Bukkit.getOnlinePlayers()) {
				ot.sendMessage(msg);
			}
			return;
		}
		if (!Game.getInstance().getState().equals(Game.State.GAME)) {
			sender.sendMessage(Lang.get("commandChat.locked"));
			return;
		}
		if (msg.length() == 1) {
			sender.sendMessage(Lang.get("commandChat.empty"));
			return;
		}
		GamePlayer cur = GameUtils.getPlayer(sender.getName());
		msg = getMessageText(sender, msg, false);
		for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
			if (gp.getTeam() == cur.getTeam()) {
				gp.getPlayer().sendMessage(msg);
			}
		}
	}
}
