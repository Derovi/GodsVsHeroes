package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
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
import org.bukkit.util.Vector;
import org.fusesource.jansi.Ansi;

import java.util.*;


import static by.dero.gvh.model.Drawings.spawnFirework;
import static by.dero.gvh.utils.DataUtils.*;
import static java.lang.Math.random;

public class GameEvents implements Listener {
    public HashMap<LivingEntity, LivingEntity> getDamageCause() {
        return damageCause;
    }

    private final HashMap<LivingEntity, LivingEntity> damageCause = new HashMap<>();
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

            itemInHand.getSummonedEntityIds().add(proj.getUniqueId());
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
            if (itemInHand instanceof UltimateInterface) {
                if (itemInHand.getCooldown().isReady()) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item.setAmount(item.getAmount()-1);
                    ((UltimateInterface)itemInHand).onPlayerInteract(event);
                }
            } else {
                if (itemInHand.getCooldown().isReady()) {
                    double radius = 0.7;
                    final int parts = 5;
                    final Vector st = new Vector(random(), random(), random()).normalize();
                    final Vector dir = player.getLocation().getDirection();
                    final Location center = player.getEyeLocation().clone().add(dir.clone().multiply(3));
                    for (int ticks = 0; ticks < 5; ticks++) {
                        for (int i = 0; i < parts; i++) {
                            final double angle = Math.PI * 2 / parts * i;
                            final Vector at = st.clone().crossProduct(dir).normalize().
                                    rotateAroundAxis(dir, angle).multiply(radius);
                            at.add(center.toVector());
                            player.getWorld().spawnParticle(Particle.FLAME,
                                    new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                                    1,0,0,0,0);
                        }
                        radius += 0.3;
                    }

                    for (int i = 0; i < 20; i++) {
                        final double angle = Math.PI / 10 * i;
                        final Vector at = st.clone().crossProduct(dir).normalize().
                                rotateAroundAxis(dir, angle).multiply(radius);
                        at.add(center.toVector());
                        player.getWorld().spawnParticle(Particle.FLAME,
                                new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                                1,0,0,0,0);
                    }
                }
                ((PlayerInteractInterface)itemInHand).onPlayerInteract(event);
            }
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
                if (item.getSummonedEntityIds().contains(event.getEntity().getUniqueId()) &&
                        item instanceof ProjectileHitInterface) {
                    ((ProjectileHitInterface) item).onProjectileHit(event);
                    if (event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {
                        ((ProjectileHitInterface) item).onProjectileHitEnemy(event);
                    }
                }
            }
        }

        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityTakeUnregisteredDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        final LivingEntity entity = (LivingEntity) event.getEntity();
        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(0);

        if ((event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) &&
                getLastLightningTime() + 200 > System.currentTimeMillis()) {
            damageCause.put(entity, getLastUsedLightning());
        }
    }

    @EventHandler
    public void onPlayerTakeRegisteredDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || event.getFinalDamage() == 0) {
            return;
        }
        final LivingEntity entity = (LivingEntity) event.getEntity();
        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(0);
        if (event.getDamager() instanceof LivingEntity) {
            damageCause.put(entity, (LivingEntity) event.getDamager());
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
        LivingEntity kil = damageCause.getOrDefault(player, player);
        if (player.getKiller() != null) {
            kil = player.getKiller();
        }

        game.onPlayerKilled(player, kil);
        game.getPlayerDeathLocations().put(player.getName(), player.getLocation());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), () -> {
            player.spigot().respawn();
            game.spawnPlayer(game.getPlayers().get(player.getName()), game.getInfo().getRespawnTime());
            player.setExp(exp);
        }, 1L);
        damageCause.remove(player);
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
