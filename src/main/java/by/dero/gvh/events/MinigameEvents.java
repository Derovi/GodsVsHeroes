package by.dero.gvh.events;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Minigame;
import by.dero.gvh.Plugin;
import by.dero.gvh.game.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.DataUtils;
import org.bukkit.Bukkit;
import by.dero.gvh.model.interfaces.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

import static by.dero.gvh.utils.DataUtils.getLastLightningTime;
import static by.dero.gvh.utils.DataUtils.getLastUsedLightning;


public class MinigameEvents implements Listener {
    private final HashMap<Player, LivingEntity> damageCause = new HashMap<>();

    @EventHandler
    public void onEntityShootBow(org.bukkit.event.entity.EntityShootBowEvent event) {
        if ((event.getEntity() instanceof Player)) {
            String playerName = event.getEntity().getName();
            GamePlayer gp = Minigame.getInstance().getGame().getPlayers().get(playerName);
            Item selectedItem = gp.getSelectedItem();
            if (selectedItem instanceof PlayerShootBowInterface) {
                ((PlayerShootBowInterface) selectedItem).onPlayerShootBow(event);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if (proj.getShooter() instanceof Player) {
            String shooterName = ((Player) proj.getShooter()).getName();
            GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
            Item itemInHand = gamePlayer.getSelectedItem();
            if (itemInHand == null) {
                return;
            }
            if (itemInHand instanceof ProjectileLaunchInterface) {
                ((ProjectileLaunchInterface)itemInHand).onProjectileLaunch(event);
            }
            itemInHand.getSummonedEntityIds().add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        String shooterName = event.getPlayer().getName();
        GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
        Item itemInHand = gamePlayer.getSelectedItem();
        if (itemInHand instanceof PlayerInteractInterface) {
            if (itemInHand instanceof InfiniteReplenishInterface) {
                event.getPlayer().getInventory().getItemInMainHand().setAmount(2);
            }
            if (itemInHand instanceof UltimateInterface) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                item.setAmount(item.getAmount()-1);
            }
            ((PlayerInteractInterface)itemInHand).onPlayerInteract(event);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            String shooterName = ((Player) event.getEntity().getShooter()).getName();
            GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
            for (Item item : gamePlayer.getItems().values()) {
                if (item.getSummonedEntityIds().contains(event.getEntity().getUniqueId())) {
                    if (item instanceof ProjectileHitInterface) {
                        ((ProjectileHitInterface) item).onProjectileHit(event);
                    }
                    item.getSummonedEntityIds().remove(event.getEntity().getUniqueId());
                }
            }
        }
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            String playerName = event.getHitEntity().getName();
            GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(playerName);
            for (Item item : gamePlayer.getItems().values()) {
                if (item instanceof ProjectileHitInterface) {
                    ((ProjectileHitInterface) item).onProjectileHitEnemy(event);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTakeUnregisteredDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING &&
                getLastLightningTime() + 200 > System.currentTimeMillis()) {
            damageCause.put(player, getLastUsedLightning());
        } else {
            damageCause.put(player, player);
        }
    }

    @EventHandler
    public void onPlayerTakeRegisteredDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        damageCause.put(player, (LivingEntity) event.getDamager());
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        Game game = Minigame.getInstance().getGame();
        Player player = event.getEntity();
        game.onPlayerKilled(player, damageCause.getOrDefault(player, null));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), () -> {
            player.spigot().respawn();
            game.respawnPlayer(game.getPlayers().get(player.getName()));
        }, 1L);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Plugin.getInstance().getPlayerData().isPlayerRegistered(event.getPlayer().getName())) {
            Plugin.getInstance().getPlayerData().registerPlayer(event.getPlayer().getName());
            // unlocking default class
            Plugin.getInstance().getPlayerData().unlockClass(event.getPlayer().getName(), "default");
        }
        Minigame.getInstance().getGame().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        for (PotionEffect pt : p.getActivePotionEffects()) {
            p.removePotionEffect(pt.getType());
        }
        Minigame.getInstance().getGame().removePlayer(p.getName());
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
