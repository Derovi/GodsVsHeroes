package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import org.bukkit.Bukkit;
import by.dero.gvh.model.interfaces.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import java.util.HashMap;


import static by.dero.gvh.utils.DataUtils.*;

public class GameEvents implements Listener {
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
            final Player player = (Player) proj.getShooter();
            final String shooterName = player.getName();
            final GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
            final Item itemInHand = gamePlayer.getSelectedItem();
            if (itemInHand == null) {
                return;
            }
            if (itemInHand instanceof ProjectileLaunchInterface) {
                ((ProjectileLaunchInterface)itemInHand).onProjectileLaunch(event);
            }

            if (itemInHand instanceof InfiniteReplenishInterface) {
                final ItemStack curItem = player.getInventory().getItemInMainHand();
                final String itemName = itemInHand.getInfo().getDisplayName();

                if (curItem.getAmount() == 1) {
                    final ItemStack pane = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                    pane.setAmount(1);
                    final ItemMeta meta = pane.getItemMeta();
                    meta.setDisplayName(itemName);
                    pane.setItemMeta(meta);
                    Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(),
                            ()-> player.getInventory().setItem(player.getInventory().getHeldItemSlot(), pane), 1);
                }
                Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> {
                    for (int slot = 0; slot < 36; slot++) {
                        final ItemStack cur = player.getInventory().getItem(slot);
                        if (cur == null) {
                            continue;
                        }
                        if (cur.getItemMeta().getDisplayName().equals(itemName)) {
                            if (cur.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
                                player.getInventory().setItem(slot, itemInHand.getItemStack());
                                player.getInventory().getItem(slot).setAmount(1);
                                break;
                            } else {
                                if (cur.getAmount() < itemInHand.getInfo().getAmount()){
                                    cur.setAmount(cur.getAmount()+1);
                                    break;
                                }
                            }
                        }
                    }
                }, itemInHand.getCooldown().getDuration());
            }

            itemInHand.getSummonedEntityIds().add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        String shooterName = event.getPlayer().getName();
        GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
        Item itemInHand = gamePlayer.getSelectedItem();
        Player player = event.getPlayer();
        if (itemInHand == null) {
            return;
        }
        if (itemInHand instanceof PlayerInteractInterface) {
            if (itemInHand instanceof UltimateInterface && itemInHand.getCooldown().isReady()) {
                ItemStack item = player.getInventory().getItemInMainHand();
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
        if (event.getHitEntity() instanceof Arrow) {
            event.getHitEntity().remove();
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
        if (!(event.getEntity() instanceof Player) || event.getFinalDamage() == 0) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (event.getDamager() instanceof LivingEntity) {
            damageCause.put(player, (LivingEntity) event.getDamager());
        }
    }


    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        event.getDrops().clear();
    }
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final float exp = player.getExp();

        final ItemStack[] inv = player.getInventory().getContents();
        event.setDeathMessage(null);
        final Game game = Minigame.getInstance().getGame();
        LivingEntity kil = player.getKiller();
        if (kil == null) {
            kil = damageCause.getOrDefault(player, player);
        }
        game.onPlayerKilled(player, kil);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), () -> {
            player.spigot().respawn();
            game.respawnPlayer(game.getPlayers().get(player.getName()));
            player.setExp(exp);
        }, 1L);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().setHealth(20);
        Minigame.getInstance().getGame().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        for (PotionEffect pt : p.getActivePotionEffects()) {
            p.removePotionEffect(pt.getType());
        }
        Minigame.getInstance().getGame().removePlayer(p.getName());
    }

    @EventHandler
    public void removeEntities(EntitySpawnEvent event) {
        final Entity ent = event.getEntity();
        if (ent instanceof LivingEntity &&
                !(ent instanceof Player)) {
            ent.remove();
        }
    }
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
