package by.dero.gvh.minigame.flagCapture;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.GameInfo;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.cristalix.core.build.BuildWorldState;
import ru.cristalix.core.build.models.Point;

import java.util.ArrayList;
import java.util.Collection;

public class FlagCapture extends Game implements DisplayInteractInterface {
	@Getter private final FlagCaptureInfo flagCaptureInfo;
	private final FlagPointManager flagPointManager;
	@Getter private static FlagCapture instance;
	
	@Getter @Setter
	private int[] flagsCaptured;
	
	public FlagCapture(GameInfo info, FlagCaptureInfo flagCaptureInfo) {
		super(info);
		instance = this;
		this.flagCaptureInfo = flagCaptureInfo;
		this.flagPointManager = new FlagPointManager(this);
	}
	
	@Override
	public boolean load() {
		return super.load();
	}
	
	@Override
	public void setDisplays() {
		for (final GamePlayer gp : getPlayers().values()) {
			final Board board = new Board(Lang.get("game.ether"), getInfo().getTeamCount() + 9);
			final Scoreboard sb = board.getScoreboard();
			for (int team = 0; team < getInfo().getTeamCount(); team++) {
				final String t = team + "hp";
				Team currentTeam = sb.registerNewTeam(t);
				currentTeam.setPrefix(Lang.get("commands." + (char)('1' + team)).substring(0, 2));
				currentTeam.setCanSeeFriendlyInvisibles(false);
			}
			final Objective obj = sb.registerNewObjective("health", "health");
			obj.setDisplayName("§c❤");
			obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
			for (final GamePlayer othergp : getPlayers().values()) {
				final String name = othergp.getPlayer().getName();
				sb.getTeam(othergp.getTeam() + "hp").addEntry(name);
			}
			gp.setBoard(board);
		}
	}
	
	@Override
	public void updateDisplays() {
		int cnt = getInfo().getTeamCount();
		ArrayList<Integer> idxs = new ArrayList<>();
		for (int i = 0; i < cnt; i++) {
			idxs.add(i);
		}
		idxs.sort((a, b) -> flagsCaptured[b] - flagsCaptured[a]);
		String[] str = new String[cnt + 9];
		for (int i = 0; i < cnt; ++i) {
			final int team = idxs.get(i);
			final String com = Lang.get("commands." + (char)('1' + team));
			str[i] = Lang.get("commands.stat").replace("%col%", String.valueOf(com.charAt(1)))
					.replace("%com%", com)
					.replace("%pts%", flagsCaptured[team] +
							" (" + (int) ((double) flagsCaptured[team] / flagCaptureInfo.getEtherToWin() * 100) + "%)");
		}
		str[cnt+1] = "";
		str[cnt+2] = " ";
		str[cnt+7] = " ";
		str[cnt+8] = Lang.get("game.online").replace("%online%", String.valueOf(getPlayers().size()));
		for (final GamePlayer gp : getPlayers().values()) {
			GamePlayerStats stats = getStats().getPlayers().get(gp.getPlayer().getName());
			str[cnt] = Lang.get("commands.playingFor").
					replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
//            str[cnt+2] = Lang.get("game.classSelected").replace("%class%", Lang.get("classes." + gp.getClassName()));
			double mult = getMultiplier(gp);
			str[cnt+3] = (mult == 1 ? Lang.get("game.expGained") : Lang.get("game.expGainedMult").
					replace("%mul%", String.format("%.1f", mult))).replace("%exp%", GameUtils.getString(stats.getExpGained()));
			str[cnt+4] = Lang.get("game.kills").replace("%kills%", String.valueOf(stats.getKills()));
			str[cnt+5] = Lang.get("game.deaths").replace("%deaths%", String.valueOf(stats.getDeaths()));
			str[cnt+6] = Lang.get("game.assists").replace("%assists%", String.valueOf(stats.getAssists()));
			gp.getBoard().update(str);
		}
	}
	
	@Override
	protected void onPlayerRespawned(GamePlayer gp) {
		super.onPlayerRespawned(gp);
	}
	
	@Override
	public void start() {
		super.start();
		
		flagPointManager.load();
		final int teams = getInfo().getTeamCount();
		flagsCaptured = new int[teams];
		for (int index = 0; index < teams; ++index) {
			flagsCaptured[index] = 0;
		}
		
		setDisplays();
		updateDisplays();
	}
	
	@Override
	public void finish(int winnerTeam, boolean needFireworks) {
		super.finish(winnerTeam, needFireworks);
		
		getStats().getPercentToWin().ensureCapacity(getInfo().getTeamCount());
		for (int i = 0; i < getInfo().getTeamCount(); i++) {
			getStats().getPercentToWin().add(flagsCaptured[i] * 100 / flagCaptureInfo.getEtherToWin());
		}
		
		Plugin.getInstance().getGameStatsData().saveGameStats(getStats());
	}
	
	@Override
	public boolean unload () {
		if (!loaded) {
			return false;
		}
		flagPointManager.unload();
		for (final GamePlayer gp : getPlayers().values()) {
			gp.getBoard().clear();
		}
		return super.unload();
	}
	
	@Override
	public void prepareMap(BuildWorldState state) {
		flagCaptureInfo.setFlagPoints(new IntPosition[state.getPoints().get("col").size()]);
		for (Point point : state.getPoints().get("col")) {
			System.out.println("col: " + point.getTag() + "|" + point.getV3().toString());
			flagCaptureInfo.getFlagPoints()[Integer.parseInt(point.getTag()) - 1] =
					new IntPosition((int) point.getV3().getX(),
							(int) point.getV3().getY(),
							(int) point.getV3().getZ());
		}
	}
	
	@Override
	public void onPlayerKilled(GamePlayer player, GamePlayer killer, Collection<GamePlayer> assists) {
		super.onPlayerKilled(player, killer, assists);
		try {
			if (!player.equals(killer)) {
				getRewardManager().give("killEnemy", killer.getPlayer(), "");
				
				MessagingUtils.sendSubtitle(Lang.get("rewmes.kill").
								replace("%exp%", GameUtils.getString(getMultiplier(killer) * getRewardManager().get("killEnemy").getCount())),
						killer.getPlayer(), 0, 20, 0);
				
				String kilCode = GameUtils.getTeamColor(killer.getTeam());
				String tarCode = GameUtils.getTeamColor(player.getTeam());
				String kilClass = Lang.get("classes." + killer.getClassName()) +
						" " + new HeroLevel(killer.getPlayerInfo(), killer.getClassName()).getRomeLevel();
				String tarClass = Lang.get("classes." + player.getClassName()) +
						" " + new HeroLevel(player.getPlayerInfo(), player.getClassName()).getRomeLevel();
				Bukkit.getServer().broadcastMessage(Lang.get("game.killGlobalMessage").
						replace("%kilCode%", kilCode).replace("%kilClass%", kilClass).
						replace("%killer%", killer.getPlayer().getName()).
						replace("%tarCode%", tarCode).replace("%tarClass%", tarClass).
						replace("%target%", player.getPlayer().getName()));
				
				if (assists != null) {
					for (GamePlayer pl : assists) {
						getRewardManager().give("assist", pl.getPlayer(), "");
						MessagingUtils.sendSubtitle(Lang.get("rewmes.assist").
										replace("%exp%", GameUtils.getString(getMultiplier(pl) * getRewardManager().get("assist").getCount())),
								pl.getPlayer(), 0, 20, 0);
					}
				}
				getGameStatsManager().addKill(player, killer, assists);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void addItems(GamePlayer player) {
		super.addItems(player);
		player.getPlayer().getInventory().setItem(8, new ItemStack(Material.COMPASS));
	}
	
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) {
		if (getState() != Game.State.GAME) {
			return;
		}
		event.setDeathMessage(null);
		final Player player = event.getEntity();
		final float exp = player.getExp();
		
		Location loc = player.getLocation();
		loc.setWorld(getWorld());
		
		getPlayerDeathLocations().put(player.getName(), player.getLocation());
		spawnPlayer(getPlayers().get(player.getName()), getInfo().getRespawnTime());
		
		player.spigot().respawn();
		player.setExp(exp);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(getPlayerDeathLocations().get(event.getPlayer().getName()));
	}
}
