package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.HeroLevel;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.cristalix.core.display.IDisplayService;
import ru.cristalix.core.event.AsyncPlayerChatEvent;
import ru.cristalix.core.permissions.IGroup;
import ru.cristalix.core.permissions.IPermissionService;
import ru.cristalix.core.scoreboard.IScoreboardService;

public class CommandChat implements Listener {
	private String getMessageText(Player player, String text, boolean global) {
		if (!Minigame.getInstance().getGame().getState().equals(Game.State.GAME)) {
			IGroup iGroup = IPermissionService.get().getBestGroup(player.getUniqueId()).join();
			String nameColor = IPermissionService.get().getNameColor(player.getUniqueId()).join();
			if (nameColor == null) {
				nameColor = iGroup.getNameColor();
			}
			return iGroup.getPrefixColor() + iGroup.getPrefix() + " " + iGroup.getNameColor() +
					nameColor + player.getName() + " §8» " + iGroup.getChatMessageColor() + text;
		}
		GamePlayer gp = GameUtils.getPlayer(player.getName());
		if (!global) {
			IGroup iGroup = IPermissionService.get().getBestGroup(player.getUniqueId()).join();
			String nameColor = IPermissionService.get().getNameColor(player.getUniqueId()).join();
			if (nameColor == null) {
				nameColor = iGroup.getNameColor();
			}
			return GameUtils.getTeamColor(gp.getTeam()) + "T §8|" + GameUtils.getTeamColor(gp.getTeam()) +
					Lang.get("classes." + gp.getClassName()) +
					" " + new HeroLevel(gp.getPlayerInfo(), gp.getClassName()).getRomeLevel() +
					"§8| " + MessagingUtils.getPrefixAddition(iGroup, nameColor) +
					player.getName() + " §8» " + iGroup.getChatMessageColor() + text;
		} else {
			IGroup iGroup = IPermissionService.get().getBestGroup(player.getUniqueId()).join();
			String nameColor = IPermissionService.get().getNameColor(player.getUniqueId()).join();
			if (nameColor == null) {
				nameColor = iGroup.getNameColor();
			}
			return GameUtils.getTeamColor(gp.getTeam()) + "G §8|" + GameUtils.getTeamColor(gp.getTeam()) +
					Lang.get("classes." + gp.getClassName()) +
					" §l" + new HeroLevel(gp.getPlayerInfo(), gp.getClassName()).getRomeLevel() +
					"§8| " + MessagingUtils.getPrefixAddition(iGroup, nameColor) +
					player.getName() + " §8» " + iGroup.getChatMessageColor() + text;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		event.setCancelled(true);
		String msg = event.getOriginalMessage()[0].toPlainText();
		if (msg.isEmpty()) {
			return;
		}
		Player sender = event.getPlayer();
		if (msg.startsWith("!") || !(Plugin.getInstance().getPluginMode() instanceof Minigame) ||
				!Game.getInstance().getState().equals(Game.State.GAME)) {
			if (msg.startsWith("!")) {
				msg = msg.substring(1);
			}
			msg = getMessageText(sender, msg, true);
			for (Player ot : Bukkit.getOnlinePlayers()) {
				ot.sendMessage(msg);
			}
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
