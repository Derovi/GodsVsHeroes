package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import org.bukkit.*;
import by.dero.gvh.model.interfaces.*;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;


import static by.dero.gvh.model.Drawings.spawnFirework;
import static by.dero.gvh.utils.DataUtils.*;

public class GameEvents implements Listener {
    private final HashMap<Player, LivingEntity> damageCause = new HashMap<>();
    private final HashSet<UUID> projectiles = new HashSet<>();
    private static Game game;
    private static final Color[] colors = new Color[] {
            Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON,
            Color.NAVY, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.YELLOW, Color.WHITE
    };

    public static void setGame(Game game) {
        GameEvents.game = game;
    }

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
            final int heldSlot = player.getInventory().getHeldItemSlot();
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
                            ()-> player.getInventory().setItem(heldSlot, pane), 1);
                }
                if (curItem.getAmount() == itemInHand.getInfo().getAmount()) {
                    final BukkitRunnable runnable = new BukkitRunnable() {
                        final PlayerInventory inv = player.getInventory();
                        final int slot = inv.getHeldItemSlot();
                        @Override
                        public void run() {
                            if (!player.isOnline()) {
                                this.cancel();
                                return;
                            }
                            if (inv.getItem(slot).getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
                                inv.setItem(slot, itemInHand.getItemStack());
                                inv.getItem(slot).setAmount(1);
                            } else {
                                inv.getItem(slot).setAmount(inv.getItem(slot).getAmount()+1);
                            }
                            if (inv.getItem(slot).getAmount() == itemInHand.getInfo().getAmount()) {
                                this.cancel();
                            }
                        }
                    };
                    final long cd = itemInHand.getCooldown().getDuration();
                    runnable.runTaskTimer(Plugin.getInstance(), cd, cd);
                    game.getRunnables().add(runnable);
                }
            }

            if (!proj.getType().equals(EntityType.SPLASH_POTION)) {
                projectiles.add(proj.getUniqueId());
                new BukkitRunnable() {
                    final Particle.DustOptions dust = new Particle.DustOptions(
                            colors[new Random().nextInt(colors.length)], 2);
                    @Override
                    public void run() {
                        if (!projectiles.contains(proj.getUniqueId())) {
                            this.cancel();
                        }
                        final Location loc = proj.getLocation();
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(),
                                0, 0, 0, 0, dust);
                    }
                }.runTaskTimer(Plugin.getInstance(), 0, 1);
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
        final Projectile proj = event.getEntity();
        projectiles.remove(proj.getUniqueId());
        if (proj.getShooter() instanceof Player) {
            String shooterName = ((Player) proj.getShooter()).getName();
            GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
            for (Item item : gamePlayer.getItems().values()) {
                if (item.getSummonedEntityIds().contains(event.getEntity().getUniqueId())) {
                    if (item instanceof ProjectileHitInterface) {
                        ((ProjectileHitInterface) item).onProjectileHit(event);
                    }
                    item.getSummonedEntityIds().remove(proj.getUniqueId());
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
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
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
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        final float exp = player.getExp();

        spawnFirework(player.getLocation().clone().add(0,1,0), 1);

        final Game game = Minigame.getInstance().getGame();

        LivingEntity kil = player.getKiller();
        if (kil == null) {
            kil = damageCause.getOrDefault(player, player);
        }
        game.onPlayerKilled(player, kil);
        game.getPlayerDeathLocations().put(player.getName(), player.getLocation());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), () -> {
            player.spigot().respawn();
            game.spawnPlayer(game.getPlayers().get(player.getName()), game.getInfo().getRespawnTime());
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
        Minigame.getInstance().getGame().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
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
