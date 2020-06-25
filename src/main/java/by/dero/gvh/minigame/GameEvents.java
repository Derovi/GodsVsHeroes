package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.utils.DirectedPosition;
import org.bukkit.*;
import by.dero.gvh.model.interfaces.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static by.dero.gvh.utils.DataUtils.*;

public class GameEvents implements Listener {
    public static void setGame(DeathMatch game) {
        GameEvents.game = game;
    }

    public HashMap<LivingEntity, LivingEntity> getDamageCause() {
        return damageCause;
    }

    private final HashMap<LivingEntity, LivingEntity> damageCause = new HashMap<>();
    private final HashSet<UUID> projectiles = new HashSet<>();
    private static DeathMatch game;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if (proj.getShooter() instanceof Player) {
            final Player player = (Player) proj.getShooter();
            final String shooterName = player.getName();
            final GamePlayer gamePlayer = getPlayer(shooterName);
            final Item itemInHand = gamePlayer.getLastUsed();
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

                final int flag = (curItem.getType().equals(Material.SNOW_BALL) ? 1 : 0);
                final int need = itemInHand.getInfo().getAmount();
                if (curItem.getAmount() == flag) {
                    final ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
                    pane.setAmount(1);
                    final ItemMeta meta = pane.getItemMeta();
                    meta.setDisplayName(itemName);
                    pane.setItemMeta(meta);
                    Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(),
                            ()-> player.getInventory().setItem(heldSlot, pane), 1);
                }

                if (curItem.getAmount() == need - 1 + flag) {
                    final BukkitRunnable runnable = new BukkitRunnable() {
                        final PlayerInventory inv = player.getInventory();
                        final int slot = inv.getHeldItemSlot();
                        @Override
                        public void run() {
                            if (!player.isOnline()) {
                                this.cancel();
                                return;
                            }
                            if (inv.getItem(slot) == null) {
                                return;
                            }
                            if (inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
                                inv.setItem(slot, itemInHand.getItemStack());
                                inv.getItem(slot).setAmount(1);
                            } else {
                                inv.getItem(slot).setAmount(inv.getItem(slot).getAmount()+1);
                            }
                            if (inv.getItem(slot).getAmount() == need) {
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
                    final Random rnd = new Random();
                    final int red = rnd.nextInt(256);
                    final int green = rnd.nextInt(256);
                    final int blue = rnd.nextInt(256);
                    @Override
                    public void run() {
                        if (!projectiles.contains(proj.getUniqueId())) {
                            this.cancel();
                        }
                        final Location loc = proj.getLocation();
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(),
                                0, red, green, blue, 1);
                    }
                }.runTaskTimer(Plugin.getInstance(), 0, 1);
            }

            itemInHand.getSummonedEntityIds().add(proj.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        String shooterName = event.getPlayer().getName();
        GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
        Item itemInHand = gamePlayer.getSelectedItem();
        Player player = event.getPlayer();
        if (itemInHand == null) {
            return;
        }
        gamePlayer.setLastUsed(itemInHand);
        if (itemInHand instanceof PlayerInteractInterface) {
            if (itemInHand instanceof UltimateInterface) {
                if (itemInHand.getCooldown().isReady()) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item.setAmount(item.getAmount()-1);
                    ((UltimateInterface)itemInHand).onPlayerInteract(event);
                }
            } else {
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
        if (event.getEntity() instanceof Firework) {
            event.setCancelled(true);
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();
        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(0);

        if (event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)  &&
                getLastLightningTime() + 100 > System.currentTimeMillis()) {
            final Player player = getLastUsedLightning();
            if (isEnemy(entity, getPlayer(player.getName()).getTeam())) {
                game.getStats().addDamage(entity, player, event.getDamage());
                damageCause.put(entity, player);
            } else {
                event.setCancelled(true);
            }
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
            if (event.getDamager() instanceof Player &&
                    isEnemy(entity, getPlayer(event.getDamager().getName()).getTeam())) {
                game.getStats().addDamage(entity, (LivingEntity) event.getDamager(), event.getDamage());
                damageCause.put(entity, (LivingEntity) event.getDamager());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Game game = Minigame.getInstance().getGame();
        for (Block block : event.blocks) {
            game.getMapManager().blockDestroyed(block.getX(), block.getY(), block.getZ(), 10000, block.getType());
        }
    }

    @EventHandler
    public void onFallingBlockLand(final EntityChangeBlockEvent e) {
        if (e.getEntity() == null) {
            return;
        }
        if (e.getEntity().getCustomName() == null) {
            return;
        }
        if (e.getEntity().getCustomName().equals("#falling_block")) {
            e.getEntity().remove();
            if (e.getBlock() != null) {
                e.getBlock().setType(Material.AIR);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {

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
                !(ent instanceof Player) &&
                !(ent instanceof ArmorStand)) {
            ent.remove();
        }
    }

    private static DirectedPosition[] borders = null;
    private static String desMsg;
    @EventHandler
    public void checkBorders(PlayerMoveEvent event) {
        if (borders == null) {
            borders = game.getInfo().getMapBorders();
            desMsg = Lang.get("game.desertionMessage");
        }
        final Player player = event.getPlayer();
        final Location loc = player.getLocation();
        if (loc.getX() < borders[0].getX()) {
            player.setVelocity(new Vector(2, 0, 0));
            player.sendMessage(desMsg);
        }
        if (loc.getX() > borders[1].getX()) {
            player.setVelocity(new Vector(-2, 0, 0));
            player.sendMessage(desMsg);
        }
        if (loc.getZ() < borders[0].getZ()) {
            player.setVelocity(new Vector(0, 0, 2));
            player.sendMessage(desMsg);
        }
        if (loc.getZ() > borders[1].getZ()) {
            player.setVelocity(new Vector(0, 0, -2));
            player.sendMessage(desMsg);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        event.setCancelled(true);
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
