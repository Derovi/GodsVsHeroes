package by.dero.gvh.minigame;

import by.dero.gvh.ChargesManager;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.*;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GameEvents implements Listener {
    public static void setGame(Game game) {
        GameEvents.game = game;
    }

    public HashMap<LivingEntity, LivingEntity> getDamageCause() {
        return damageCause;
    }

    private final HashMap<LivingEntity, LivingEntity> damageCause = new HashMap<>();

    public HashSet<UUID> getProjectiles() {
        return projectiles;
    }

    private final HashSet<UUID> projectiles = new HashSet<>();
    private final HashMap<UUID, Location> lastPos = new HashMap<>();
    private static Game game;

    @EventHandler
    public void onEntityShootBow(org.bukkit.event.entity.EntityShootBowEvent event) {
        if ((event.getEntity() instanceof Player)) {
            String playerName = event.getEntity().getName();
            GamePlayer gp = Minigame.getInstance().getGame().getPlayers().get(playerName);
            Item selectedItem = gp.getLastUsed();
            if (selectedItem instanceof PlayerShootBowInterface) {
                ((PlayerShootBowInterface) selectedItem).onPlayerShootBow(event);
            }
            selectedItem.getSummonedEntityIds().add(event.getProjectile().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if (!(proj instanceof Arrow) && (proj.getShooter() instanceof Player) &&
                !(proj.hasMetadata("custom"))) {
            event.setCancelled(true);
        } else {
            projectiles.add(proj.getUniqueId());
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
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) ||
            event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);
            return;
        }

        if (itemInHand.getInfo().getMaterial() != Material.BOW) {
            event.setCancelled(true);
        }
        gamePlayer.setLastUsed(itemInHand);
        if (itemInHand instanceof PlayerInteractInterface) {
            if (itemInHand instanceof UltimateInterface) {
                if (itemInHand.getCooldown().isReady()) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item.setAmount(item.getAmount()-1);
                    ((UltimateInterface) itemInHand).onPlayerInteract(event);
                }
            } else {
                if (itemInHand instanceof InfiniteReplenishInterface) {
                    if (!itemInHand.getCooldown().isReady() || !ChargesManager.getInstance().consume(player, itemInHand)) {
                        return;
                    }
                }
                ((PlayerInteractInterface) itemInHand).onPlayerInteract(event);
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
                if (item.getSummonedEntityIds().contains(event.getEntity().getUniqueId())) {
                    if (item instanceof ProjectileHitInterface) {
                        ((ProjectileHitInterface) item).onProjectileHit(event);
                        if (event.getHitEntity() != null && GameUtils.isEnemy(event.getHitEntity(), gamePlayer.getTeam())) {
                            ((ProjectileHitInterface) item).onProjectileHitEnemy(event);
                        }
                    }
                    item.getSummonedEntityIds().remove(event.getEntity().getUniqueId());
                }
            }
        }

        if (event.getEntity() instanceof Arrow) {
            new BukkitRunnable() {
                @Override
                public void run () {
                    event.getEntity().remove();
                }
            }.runTaskLater(Plugin.getInstance(), 60);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        for (SneakInterface item : GameUtils.selectItems(GameUtils.getPlayer(player.getName()), SneakInterface.class)) {
            item.onPlayerSneak();
        }
    }

    @EventHandler
    public void onEntityTakeUnregisteredDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FALL) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL)) {
            event.setCancelled(true);
            return;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();

        if ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) &&
                entity.getHealth() <= event.getFinalDamage()) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING) &&
                GameUtils.getLastLightningTime() + 100 > System.currentTimeMillis()) {
            final Player player = GameUtils.getLastUsedLightning();
            if (GameUtils.isEnemy(entity, GameUtils.getPlayer(player.getName()).getTeam())) {
                game.getStats().addDamage(entity, player, event.getDamage());
                damageCause.put(entity, player);
            } else {
                event.setCancelled(true);
            }
        }
        ((LivingEntity) event.getEntity()).setNoDamageTicks(5);
    }

    @EventHandler
    public void onPlayerTakeRegisteredDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) ||
                !(event.getDamager() instanceof LivingEntity) ||
                event.getFinalDamage() == 0) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();
        LivingEntity damager = (LivingEntity) event.getDamager();
        if (!(damager instanceof Player)) {
            damager = GameUtils.getMob(damager.getUniqueId()).getOwner();
        }
        if (GameUtils.isEnemy(entity, event.getDamager())) {
            game.getStats().addDamage(entity, damager, event.getDamage());
            damageCause.put(entity, damager);
        } else {
            event.setCancelled(true);
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
        Player player = event.getEntity();
        final HashMap<LivingEntity, LivingEntity> damageCause = Minigame.getInstance().getGameEvents().getDamageCause();
        LivingEntity kil = damageCause.getOrDefault(player, player);
        if (player.getKiller() != null) {
            kil = player.getKiller();
        }
        if (!(kil instanceof Player)) {
            kil = GameUtils.getMob(kil.getUniqueId()).getOwner();
        }
        for (PlayerKillInterface item : GameUtils.selectItems(GameUtils.getPlayer(kil.getName()), PlayerKillInterface.class)) {
            item.onPlayerKill(player);
        }
        game.onPlayerKilled(player, kil);
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
        if (ent instanceof LivingEntity && !(ent instanceof Player) &&
                !(ent instanceof ArmorStand) && !ent.hasMetadata("custom")) {
            ent.remove();
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
    public void removeNotGameDamage(EntityDamageEvent event) {
        if (!game.getState().equals(Game.State.GAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        lastPos.put(event.getPlayer().getUniqueId(), event.getFrom());
    }

    public Location getLastPos (Player player) {
        return lastPos.getOrDefault(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void removePotions(PotionSplashEvent event) {
        event.setCancelled(true);
    }
}
