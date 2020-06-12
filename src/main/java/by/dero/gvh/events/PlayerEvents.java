package by.dero.gvh.events;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ShootBowInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onEntityShoot(org.bukkit.event.entity.EntityShootBowEvent event) {
        System.out.println("Shoot!");
        if ((event.getEntity() instanceof Player)) {
            String playerName = event.getEntity().getName();
            Item selectedItem = Plugin.getInstance().getGame().getPlayers().get(playerName).getSelectedItem();
            if (selectedItem instanceof ShootBowInterface) {
                ((ShootBowInterface) selectedItem).onPlayerShootBow(event);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            String shooterName = ((Player) event.getEntity().getShooter()).getName();
            GamePlayer gamePlayer = Plugin.getInstance().getGame().getPlayers().get(shooterName);
            Item itemInHand = gamePlayer.getSelectedItem();
            if (itemInHand == null) {
                return;
            }
            itemInHand.getSummonedEntityIds().add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            String shooterName = ((Player) event.getEntity().getShooter()).getName();
            GamePlayer gamePlayer = Plugin.getInstance().getGame().getPlayers().get(shooterName);
            for (Item item : gamePlayer.getItems().values()) {
                if (item.getSummonedEntityIds().contains(event.getEntity().getUniqueId())) {
                    if (item instanceof ProjectileHitInterface) {
                        ((ProjectileHitInterface) item).onProjectileHitEnemy(event);
                    }
                    item.getSummonedEntityIds().remove(event.getEntity().getUniqueId());
                }
            }
        }
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            String playerName = ((Player) event.getHitEntity()).getName();
            GamePlayer gamePlayer = Plugin.getInstance().getGame().getPlayers().get(playerName);
            for (Item item : gamePlayer.getItems().values()) {
                if (item instanceof ProjectileHitInterface) {
                    ((ProjectileHitInterface) item).onProjectileHit(event);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        event.getDrops().clear();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), () -> {
            event.getEntity().spigot().respawn();
            Plugin.getInstance().getGame().respawnPlayer(Plugin.getInstance().getGame().getPlayers().get(event.getEntity().getName()));
        }, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Plugin.getInstance().getGame().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Plugin.getInstance().getGame().removePlayer(event.getPlayer().getName());
    }
}
