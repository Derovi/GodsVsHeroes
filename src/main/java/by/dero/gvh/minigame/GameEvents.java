package by.dero.gvh.minigame;

import by.dero.gvh.GameMob;
import by.dero.gvh.GameObject;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.books.ItemDescriptionBook;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.*;
import by.dero.gvh.nmcapi.NMCUtils;
import by.dero.gvh.utils.CosmeticsUtils;
import by.dero.gvh.utils.Dwelling;
import by.dero.gvh.utils.GameUtils;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameEvents implements Listener {
    public static void setGame(Game game) {
        GameEvents.game = game;
    }

    @Getter private final HashMap<GamePlayer, Stack<Pair<GamePlayer, Long> > > damageCause = new HashMap<>();

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
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Projectile proj = event.getEntity();
        if (!(proj instanceof Arrow || proj instanceof FishHook) && (proj.getShooter() instanceof Player) &&
                !(proj.hasMetadata("custom"))) {
            event.setCancelled(true);
        } else {
            GamePlayer gp = GameUtils.getPlayer(((Player) event.getEntity().getShooter()).getName());
            if (gp.getSelectedItem() instanceof ProjectileLaunchInterface) {
                ((ProjectileLaunchInterface) gp.getSelectedItem()).onProjectileLaunch(event);
                projectiles.add(proj.getUniqueId());
            }
        }
    }

    private final List<Material> inventoryBlocks = Arrays.asList(
            Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST, Material.HOPPER,
            Material.HOPPER_MINECART, Material.STORAGE_MINECART
    );

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (clicked != null && inventoryBlocks.contains(clicked.getType())) {
            event.setCancelled(true);
            return;
        }
        if (!Game.getInstance().getState().equals(Game.State.GAME)) {
            event.setCancelled(true);
            return;
        }
        String shooterName = event.getPlayer().getName();
        GamePlayer gamePlayer = Minigame.getInstance().getGame().getPlayers().get(shooterName);
    
        ItemStack offItem = event.getPlayer().getInventory().getItemInOffHand();
        Item itemInHand = gamePlayer.getSelectedItem();
        if (offItem != null && !offItem.getType().equals(Material.AIR) && itemInHand instanceof DoubleHanded) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                drawOffhandAnimation(event.getPlayer());
                if (itemInHand instanceof DoubleHandInteractInterface) {
                    ((DoubleHandInteractInterface) itemInHand).interactOffHand(event);
                }
            } else if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (itemInHand instanceof DoubleHandInteractInterface) {
                    ((DoubleHandInteractInterface) itemInHand).interactMainHand(event);
                }
            }
            return;
        }
        
        for (Item item : gamePlayer.getItems().values()) {
            if (item instanceof InteractAnyItem) {
                InteractAnyItem in = (InteractAnyItem) item;
                if (in.playerInteract()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (itemInHand == null) {
            return;
        }
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) ||
            event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);
            return;
        }

        if (itemInHand.getInfo().getMaterial() != Material.BOW && itemInHand.getInfo().getMaterial() != Material.FISHING_ROD) {
            event.setCancelled(true);
        }
        gamePlayer.setLastUsed(itemInHand);
        if (gamePlayer.isDisabled()) {
            event.setCancelled(true);
            return;
        }
        if (!event.getAction().equals(Action.PHYSICAL) && itemInHand instanceof PlayerInteractInterface) {
            if (itemInHand instanceof InfiniteReplenishInterface) {
                if (!itemInHand.getCooldown().isReady() || !gamePlayer.consume(itemInHand)) {
                    return;
                }
            }
            ((PlayerInteractInterface) itemInHand).onPlayerInteract(event);
        }
    }
    
    public void drawOffhandAnimation(Player player) {
        ItemStack item = player.getInventory().getItemInOffHand();
        if (item != null) {
            if (item.getType() != Material.AIR) {
                try {
                    ItemStack mh = player.getInventory().getItemInMainHand();
                    if (mh == null || (mh.getType() != Material.BOW && mh.getType() != Material.SHIELD && !mh.getType().name().matches("TRIDENT"))) {
                        Dwelling.sendPacket(player, Dwelling.prepareVanillaPacket("PacketPlayOutAnimation", player.getEntityId(), 3));
                    }
                } catch (IllegalArgumentException | NullPointerException | AuthorNagException ignored) {
                }
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
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!Game.getInstance().getState().equals(Game.State.GAME)) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        GamePlayer gp = GameUtils.getPlayer(player.getName());
    
        if (gp.isDisabled()) {
            event.setCancelled(true);
            return;
        }
        for (SneakInterface item : GameUtils.selectItems(gp, SneakInterface.class)) {
            item.onPlayerSneak();
        }
    }

    @EventHandler
    public void onPlayerUnmount (VehicleExitEvent event) {
        for (VehicleExitInterface item : GameUtils.selectItems(GameUtils.getPlayer(event.getExited().getName()), VehicleExitInterface.class)) {
            item.onPlayerUnmount(event);
        }
    }

    @EventHandler
    public void onEntityTakeUnregisteredDamage (EntityDamageEvent event) {
        if (!Game.getInstance().getState().equals(Game.State.GAME)) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FALL) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            event.setCancelled(true);
            return;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();

        if ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) &&
                entity.getHealth() <= event.getFinalDamage()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTakeRegisteredDamage (EntityDamageByEntityEvent event) {
        if (!Game.getInstance().getState().equals(Game.State.GAME)) {
            event.setCancelled(true);
            return;
        }
        Entity ent = event.getDamager();
        if (ent instanceof Firework) {
            event.setCancelled(true);
            return;
        }
        if (ent instanceof Projectile) {
            ent = (Entity) ((Projectile) ent).getShooter();
        }
        if (!(event.getEntity() instanceof LivingEntity) ||
                !(ent instanceof LivingEntity) ||
                event.getFinalDamage() == 0) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();
        GamePlayer damager;
        if (!(ent instanceof Player)) {
            damager = GameUtils.getPlayer(GameUtils.getMob(ent.getUniqueId()).getOwner().getName());
        } else {
            damager = GameUtils.getPlayer(ent.getName());
        }
        
        GameObject gm = GameUtils.getObject(entity);
        if (gm != null && damager.getTeam() != gm.getTeam()) {
            if (gm instanceof GamePlayer) {
                damageCause.putIfAbsent((GamePlayer) gm, new Stack<>());
                damageCause.get(gm).add(Pair.of(damager, System.currentTimeMillis()));
                if (!gm.equals(damager)) {
                    Game.getInstance().getGameStatsManager().addDamage((GamePlayer) gm, damager, event.getDamage());
                }
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> {
                    if (!entity.isDead()) {
                        ((GameMob) gm).updateName();
                    }
                }, 1);
            }
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
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        event.getDrops().clear();
    }

    private final HashSet<GamePlayer> assists = new HashSet<>();
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer playerGP = GameUtils.getPlayer(player.getName());
        GamePlayer kilGP = playerGP;
        assists.clear();
        Stack<Pair<GamePlayer, Long> > cause = damageCause.getOrDefault(playerGP, null);
        if (cause != null && !cause.isEmpty()) {
            kilGP = cause.pop().getKey();
            Long time = System.currentTimeMillis();
            while (!cause.isEmpty()) {
                Pair<GamePlayer, Long> cur = cause.pop();
                if (time - cur.getValue() > 5000) {
                    break;
                } else {
                    assists.add(cur.getKey());
                }
            }
        }
        if (player.getKiller() != null) {
            kilGP = GameUtils.getPlayer(player.getKiller().getName());
        }
        assists.remove(kilGP);
        if (!kilGP.equals(playerGP)) {
            CosmeticsUtils.dropHead(player, kilGP.getPlayer());
            for (PlayerKillInterface item : GameUtils.selectItems(kilGP, PlayerKillInterface.class)) {
                item.onPlayerKill(player);
            }
        }
        
        game.onPlayerKilled(GameUtils.getPlayer(player.getName()), kilGP, new ArrayList<>(assists));
        damageCause.remove(playerGP);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
//        if (Game.getInstance().getState().equals(Game.State.GAME)) {
            Minigame.getInstance().getGame().addPlayer(event.getPlayer());
//        } else {
//            event.getPlayer().setGameMode(GameMode.SPECTATOR);
//        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        if (game.getState() == Game.State.GAME && game.getStats().getPlayers().containsKey(p.getName())) {
            game.getStats().getPlayers().get(p.getName()).setPlayTimeSec(
                    (int) (System.currentTimeMillis() / 1000 - Game.getInstance().getGameStatsManager().getStartTime()));
        }
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
        if (event.getWhoClicked() instanceof Player) {
            GamePlayer player = GameUtils.getPlayer(event.getWhoClicked().getName());
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                return;
            }
            Item item = player.getItems().getOrDefault(NMCUtils.getNBT(itemStack).getString("custom"), null);
            if (item == null || item.getName().startsWith("default")) {
                return;
            }
            ItemDescriptionBook book =
                    new ItemDescriptionBook(Plugin.getInstance().getBookManager(), player.getPlayer(),
                            player.getClassName(), item.getName());
            book.build();
            book.open();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        lastPos.put(event.getPlayer().getUniqueId(), event.getFrom());
    }

    public Location getLastPos (Player player) {
        return lastPos.getOrDefault(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void interactArmorStand(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void removePotions(PotionSplashEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        if (e.toThunderState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void removeTramplingEntity(EntityInteractEvent event) {
        if (event.getBlock() != null && event.getBlock().getType().equals(Material.SOIL)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void removeTramplingPlayer(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().equals(Material.SOIL)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void checkDoubleHanded(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
    
        ItemStack is = player.getInventory().getItem(event.getNewSlot());
        if (is != null && GameUtils.getPlayer(player.getName()).getItems().
                getOrDefault(NMCUtils.getNBT(is).getString("custom"), null) instanceof DoubleHanded) {
            player.getInventory().setItemInOffHand(is);
        } else {
            player.getInventory().setItemInOffHand(GameUtils.clearItem);
        }
    }
    
    @EventHandler
    public void removeSwapHand(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }
}
