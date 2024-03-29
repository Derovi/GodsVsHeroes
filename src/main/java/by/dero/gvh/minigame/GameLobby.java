package by.dero.gvh.minigame;

import by.dero.gvh.AdviceManager;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DisplayInteractInterface;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.BridgeUtils;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MessagingUtils;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class GameLobby implements Listener, DisplayInteractInterface {
    private final Game game;
    private int timeLeft = 61;
    private BukkitRunnable prepairing;
    private final ItemStack[] chooseInv;
    @Getter private final MapVoting mapVoting;

    public GameLobby(Game game) {
        this.game = game;
        chooseInv = new ItemStack[9];
        int cnt = game.getInfo().getTeamCount();
        for (int i = 0; i < cnt; i++) {
            String name = Lang.get("commands." + (char)('1' + i));
            chooseInv[i] = new ItemStack(Material.WOOL, 1, GameUtils.codeToData.get(name.charAt(1)));
            ItemMeta meta = chooseInv[i].getItemMeta();
            meta.setDisplayName(name);
            chooseInv[i].setItemMeta(meta);
        }
        chooseInv[8] = new ItemStack(Material.BARRIER, 1);
        ItemMeta cns = chooseInv[8].getItemMeta();
        cns.setDisplayName(Lang.get("gameLobby.exit"));
        chooseInv[8].setItemMeta(cns);
        Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
        mapVoting = new MapVoting();
    }

    private boolean ready = false;
    private final int[] showTime = {60, 45, 30, 15, 10, 5, 4, 3, 2, 1};
    
    @Override
    public void setDisplays() {
    
    }
    
    @Override
    public void updateDisplays() {
        String[] ar = new String[8 + mapVoting.getMaps().size()];
        ar[0] = Lang.get("gameLobby.boardReady").
                    replace("%cur%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())).
                    replace("%max%", String.valueOf(game.getInfo().getMaxPlayerCount()));
        ar[1] = Lang.get("gameLobby.boardRequired").replace("%min%", String.valueOf(game.getInfo().getMinPlayerCount()));
        ar[2] = Lang.get("gameLobby.boardTimeLeft").replace("%time%", String.valueOf(timeLeft));
        ar[3] = " ";
        ar[4] = Lang.get("lobby.mapVotingHint");
        for (int i = 0; i < mapVoting.getMaps().size(); i++) {
            MapVoting.Map cur = mapVoting.getMaps().get(i);
            ar[5 + i] = Lang.get("lobby.mapVoting").replace("%map%", cur.getDisplayName()).
                    replace("%val%", String.valueOf(cur.getVoteCount()));
        }
        ar[ar.length-3] = " ";
        for (final GamePlayer gp : game.getPlayers().values()) {
            ar[ar.length-2] = Lang.get("gameLobby.boardPreferred").
                    replace("%com%", Lang.get("commands." + (char)('1' + gp.getTeam())));
            ar[ar.length-1] = Lang.get("game.classSelected").replace("%class%", Lang.get("classes." + gp.getClassName()));
            gp.getBoard().update(ar);
        }
    }

    @EventHandler
    public void onPlayerChoose(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        int slot = player.getInventory().getHeldItemSlot();
        GamePlayer gp = GameUtils.getPlayer(player.getName());
        if (slot == 8) {
            BridgeUtils.redirectPlayer(player.getPlayer(), Plugin.getInstance().getServerData().getSavedLobbyServer().getName());
        } else if (!gp.setPreferredTeam(slot)) {
            player.sendMessage(Lang.get("gameLobby.cantSelect"));
        } else {
            player.getInventory().setHelmet(event.getItem());
            updateDisplays();
        }
    }

    private final HashMap<UUID, Vector> lastPos = new HashMap<>();
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
            lastPos.put(player.getUniqueId(), player.getLocation().toVector());
        } else if (player.getLocation().getY() < 30) {
            player.teleport(lastPos.get(player.getUniqueId()).toLocation(player.getWorld()));
        }
    }
    
    public void startGame() {
        PlayerMoveEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        timeLeft = 61;
        if (prepairing != null) {
            prepairing.cancel();
        }
        ready = false;
        MessagingUtils.sendTitle(Lang.get("game.gameAlreadyStarted"), game.getPlayers().values());

        for(Player online : Bukkit.getOnlinePlayers()){
            online.setHealth(online.getHealth());
        }
        game.start();
    }

    public void startPrepairing() {
        prepairing = new BukkitRunnable() {
            @Override
            public void run() {
                if (!ready) {
                    this.cancel();
                    timeLeft = 61;
                    updateDisplays();
                    return;
                }
                if (0 < timeLeft && timeLeft <= 10) {
                    World world = Minigame.getInstance().getLobbyWorld();
                    world.playSound(game.getInfo().getLobbyPosition().toLocation(world),
                           Sound.BLOCK_NOTE_PLING, 100, 1);
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() >= timeLeft) {
                            player.setHealth(timeLeft);
                        }
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(timeLeft);
                    }
                }
                timeLeft--;
                if (ArrayUtils.indexOf(showTime, timeLeft) != -1) {
                    MessagingUtils.sendTitle("§a" + timeLeft, game.getPlayers().values());
                }
                updateDisplays();

                if (timeLeft == 0) {
                    this.cancel();
                    startGame();
                }
            }
        };
        prepairing.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    public void onPlayerJoined(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

        player.setGameMode(GameMode.SURVIVAL);
        gamePlayer.setBoard(new Board("Lobby", 8 + mapVoting.maps.size()));
        for (PotionEffect effect : gamePlayer.getPlayer().getActivePotionEffects()) {
            gamePlayer.getPlayer().removePotionEffect(effect.getType());
        }

        player.getInventory().setHeldItemSlot(0);
        final int players = game.getPlayers().size();
        final int needed = game.getInfo().getMaxPlayerCount();

        Bukkit.getServer().broadcastMessage(Lang.get("gameLobby.playerJoined")
                .replace("%name%", MessagingUtils.getPrefixAddition(player) + player.getName())
                .replace("%cur%", String.valueOf(players))
                .replace("%max%", String.valueOf(needed))
        );
        updateDisplays();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(Plugin.getInstance(), gamePlayer.getPlayer());
            player.showPlayer(Plugin.getInstance(), p);
        }

        PlayerInventory inv = player.getInventory();
        inv.clear();
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, chooseInv[i]);
        }
        if (players >= game.getInfo().getMinPlayerCount() && !ready) {
            ready = true;
            startPrepairing();
        }
        if (players >= game.getInfo().getMaxPlayerCount()) {
            timeLeft = Math.min(timeLeft, 10);
        }
        AdviceManager.sendAdvice(gamePlayer.getPlayer(), "chooseTeam", 20, 240,
                (pl) -> (!game.getState().equals(Game.State.WAITING) ||
                        !game.getPlayers().containsKey(pl.getName()) ||
                        game.getPlayers().get(pl.getName()).getTeam() != -1));
    }

    public void onPlayerLeft(GamePlayer gamePlayer) {
        final int players = game.getPlayers().size() - 1;
        final int needed = game.getInfo().getMinPlayerCount();
        Bukkit.getServer().broadcastMessage(Lang.get("gameLobby.playerLeft")
                .replace("%name%", MessagingUtils.getPrefixAddition(gamePlayer.getPlayer())
                        + gamePlayer.getPlayer().getName())
                .replace("%cur%", String.valueOf(players))
                .replace("%max%", String.valueOf(game.getInfo().getMaxPlayerCount()))
        );

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), ()-> {
            updateDisplays();
            if (players < needed) {
                ready = false;
            }
        }, 2);
    }

    public Game getGame () {
        return game;
    }
}