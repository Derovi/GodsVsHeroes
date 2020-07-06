package by.dero.gvh.utils;

import by.dero.gvh.GameMob;
import by.dero.gvh.GameObject;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class GameUtils {
    private static Player lastUsedLightning;
    private static Long lastLightningTime = 0L;
    public static final double eyeHeight = 1.7775;

    public static HashMap<Character, Byte> codeToData = null;
    public static Sound[] notes = new Sound[] {
            Sound.BLOCK_NOTE_BASEDRUM,
            Sound.BLOCK_NOTE_BASS,
            Sound.BLOCK_NOTE_BELL,
            Sound.BLOCK_NOTE_CHIME,
            Sound.BLOCK_NOTE_FLUTE,
            Sound.BLOCK_NOTE_GUITAR,
            Sound.BLOCK_NOTE_HARP,
            Sound.BLOCK_NOTE_HAT,
            Sound.BLOCK_NOTE_PLING,
            Sound.BLOCK_NOTE_SNARE,
            Sound.BLOCK_NOTE_XYLOPHONE,
    };

    public GameUtils () {
        if (codeToData == null) {
            codeToData = new HashMap<>();
            codeToData.put('0', (byte) 15);
            codeToData.put('1', (byte) 11);
            codeToData.put('2', (byte) 13);
            codeToData.put('3', (byte) 9);
            codeToData.put('4', (byte) 14);
            codeToData.put('5', (byte) 10);
            codeToData.put('6', (byte) 4);
            codeToData.put('7', (byte) 8);
            codeToData.put('8', (byte) 7);
            codeToData.put('9', (byte) 11);
            codeToData.put('a', (byte) 5);
            codeToData.put('b', (byte) 3);
            codeToData.put('c', (byte) 14);
            codeToData.put('d', (byte) 2);
            codeToData.put('e', (byte) 4);
            codeToData.put('f', (byte) 0);
        }
    }

    public static void changeColor(Location loc, char code) {
        loc.getBlock().setData(codeToData.get(code));
    }

    public static GamePlayer getPlayer(String name) {
        return Minigame.getInstance().getGame().getPlayers().getOrDefault(name, null);
    }

    public static GameMob getMob(UUID uuid) {
        return Minigame.getInstance().getGame().getMobs().getOrDefault(uuid, null);
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer) {
        damage(damage, target, killer, false);
    }

    public static <T> ArrayList<T> selectItems(GamePlayer gp, Class<T> cl) {
        final ArrayList<T> list = new ArrayList<>();
        for (by.dero.gvh.model.Item item : gp.getItems().values()) {
            if (cl.isInstance(item)) {
                list.add(cl.cast(item));
            }
        }
        return list;
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer, boolean hasDelay) {
        Minigame.getInstance().getGameEvents().getDamageCause().put(target, killer);
        if (!hasDelay) {
            target.setNoDamageTicks(0);
        }
        target.damage(damage, killer);
    }

    public static boolean isDeadPlayer(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getGameMode().equals(GameMode.SPECTATOR);
        }
        return false;
    }

    public static boolean isGameEntity(Entity entity) {
        if (entity instanceof Player) {
            Player player = ((Player) entity);
            return player.getGameMode().equals(GameMode.SURVIVAL) && !getPlayer(player.getName()).isInventoryHided();
        }
        return true;
    }

    public static boolean isVoid(Material material) {
        switch (material) {
            case AIR:
            case SAPLING:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case LONG_GRASS:
            case DEAD_BUSH:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TORCH:
            case FIRE:
            case REDSTONE_WIRE:
            case CROPS:
            case LADDER:
            case RAILS:
            case LEVER:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case SNOW:
            case SUGAR_CANE_BLOCK:
            case WATER_LILY:
            case TRIPWIRE:
            case FLOWER_POT:
            case CARROT:
            case POTATO:
            case WOOD_BUTTON:
            case ACTIVATOR_RAIL:
            case CARPET:
            case DOUBLE_PLANT:
            case END_ROD:
            case CHORUS_PLANT:
            case CHORUS_FLOWER:
            case BEETROOT_BLOCK:
                return true;
            default:
                return false;
        }
    }

    public static void doubleSpaceCooldownMessage(Item item) {
        GamePlayer player = GameUtils.getPlayer(item.getOwner().getName());
        if (!player.isActionBarBlocked()) {
            player.setActionBarBlocked(true);
            MessagingUtils.sendCooldownMessage(player.getPlayer(), item.getName(), item.getCooldown().getSecondsRemaining());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setActionBarBlocked(false);
                }
            }.runTaskLater(Plugin.getInstance(), 30);
        }
    }

    public static Entity spawnEntity(final Location loc, final EntityType type) {
        CraftWorld wrld = ((CraftWorld) loc.getWorld());
        net.minecraft.server.v1_12_R1.Entity entity = wrld.createEntity(loc, type.getEntityClass());
        entity.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        wrld.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return entity.getBukkitEntity();
    }

    public static LivingEntity spawnTeamEntity(Location loc, EntityType type, GamePlayer gp) {
        LivingEntity entity = (LivingEntity) spawnEntity(loc, type);
        Game.getInstance().getMobs().put(entity.getUniqueId(), new GameMob(entity, gp.getTeam(), gp.getPlayer()));
        return entity;
    }

    public static Projectile spawnProjectile(final Location at, final double speed,
                                             final EntityType type, final Player player) {
        final Vector dir = at.getDirection().clone();

        final Location loc = at.clone().add(dir.clone().multiply(1.8));
        Projectile obj = (Projectile) spawnEntity(loc, type);
        obj.setVelocity(dir.multiply(speed));
        obj.setShooter(player);

        return obj;
    }

    public static Projectile spawnSplashPotion(Location at, double speed, PotionType type, Player player) {
        Potion instance = new Potion(type, 1, true);

        EntityPlayer pl = ((CraftPlayer) player).getHandle();
        EntityPotion potion = new EntityPotion(pl.world, pl, CraftItemStack.asCraftCopy(instance.toItemStack(1)).handle);
        potion.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(),""));
        Vector dir = at.getDirection();
        Location loc = at.clone().add(dir.clone().multiply(2));
        potion.locX = loc.x;
        potion.locY = loc.y;
        potion.locZ = loc.z;
        potion.motX = dir.x * speed;
        potion.motY = dir.y * speed;
        potion.motZ = dir.z * speed;

        potion.world.addEntity(potion, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (Projectile) potion.getBukkitEntity();
    }

    public static boolean isEnemy(final Entity ent, final int team) {
        if (ent == null) {
            return false;
        }
        if (ent instanceof ArmorStand || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof LivingEntity)) {
            return false;
        }

        GameObject obj = ent instanceof Player ? getPlayer(ent.getName()) : getMob(ent.getUniqueId());
        return obj != null && obj.getTeam() != team;
    }

    public static boolean isEnemy(Entity ent, Entity other) {
        if (ent == null || other == null) {
            return false;
        }
        if (!(ent instanceof LivingEntity) || !(other instanceof LivingEntity) ||
                ent.isDead() || other.isDead()) {
            return false;
        }

        GameObject o1 = ent instanceof Player ? getPlayer(ent.getName()) :
                Game.getInstance().getMobs().get(ent.getUniqueId());
        GameObject o2 = other instanceof Player ? getPlayer(other.getName()) :
                Game.getInstance().getMobs().get(other.getUniqueId());
        return o1 != null && o2 != null && o1.getTeam() != o2.getTeam();
    }

    public static boolean isInGame(Player player) {
        return player != null && player.isOnline() && !player.getGameMode().equals(GameMode.SPECTATOR);
    }

    public static List<LivingEntity> getNearby(final Location wh, final double radius) {
        final List<LivingEntity> buf = new ArrayList<>();
        for (Entity ent : Objects.requireNonNull(wh.getWorld()).getNearbyEntities(wh, radius, radius, radius)) {
            if (ent instanceof LivingEntity &&
                    ent.getLocation().distance(wh) <= radius) {
                buf.add((LivingEntity) ent);
            }
        }
        return buf;
    }

    public static boolean isAlly(final Entity ent, final int team) {
        if (ent == null) {
            return false;
        }
        if (ent instanceof ArmorStand || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof LivingEntity)) {
            return false;
        }
        GameObject obj = ent instanceof Player ? getPlayer(ent.getName()) : getMob(ent.getUniqueId());
        return obj != null && obj.getTeam() == team;
    }

    public static boolean isAlly(final Entity ent, final Entity other) {
        if (ent == null || other == null) {
            return false;
        }
        if (!(ent instanceof LivingEntity) || !(other instanceof LivingEntity) ||
            ent.isDead() || other.isDead()) {
            return false;
        }

        GameObject o1 = ent instanceof Player ? getPlayer(ent.getName()) :
                Game.getInstance().getMobs().get(ent.getUniqueId());
        GameObject o2 = other instanceof Player ? getPlayer(other.getName()) :
                Game.getInstance().getMobs().get(other.getUniqueId());
        return o1 != null && o2 != null && o1.getTeam() == o2.getTeam();
    }

    public static Player getLastUsedLightning() {
        return lastUsedLightning;
    }

    public static void setLastUsedLightning(Player lastUsedLightning) {
        GameUtils.lastUsedLightning = lastUsedLightning;
        GameUtils.lastLightningTime = System.currentTimeMillis();
    }

    public static Long getLastLightningTime() {
        return lastLightningTime;
    }


    public static LivingEntity getTargetEntity(final Player entity, final double maxRange) {
        return getTarget(entity, entity.getWorld().getLivingEntities(), maxRange);
    }

    public static <T extends LivingEntity> T getTarget(final Player entity,
                                                final Iterable<T> entities,
                                                final double maxRange) {
        if (entity == null)
            return null;
        T target = null;
        final double threshold = 1;
        for (final T other : entities) {
            if (other.getLocation().distance(entity.getLocation()) > maxRange) {
                continue;
            }
            final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold &&
                    n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
                if (target == null || (target.getLocation().distanceSquared(
                        entity.getLocation()) > other.getLocation()
                        .distanceSquared(entity.getLocation())) &&
                        isEnemy(target, getPlayer(entity.getName()).getTeam())) {
                    target = other;
                }
            }
        }
        return target;
    }

    public static void setInvisibleFlags(ArmorStand stand) {
        EntityArmorStand handle = ((CraftArmorStand) stand).getHandle();
        handle.setCustomNameVisible(false);
        handle.setNoGravity(true);
        handle.setInvisible(true);
        handle.canPickUpLoot = false;
        handle.noclip = true;
        handle.collides = false;
        handle.invulnerable = true;
    }

    public static boolean isInBlock(Entity entity) {
        Location loc = entity.getLocation();
        for (double i = 0; i < Math.ceil(entity.getHeight()); i++) {
            if (!loc.getBlock().getType().equals(Material.AIR)) {
                return false;
            }
            loc.add(0, 1, 0);
        }
        return false;
    }

    public static GamePlayer getNearestEnemyPlayer(GamePlayer gp) {
        Location wh = gp.getPlayer().getLocation();
        GamePlayer ret = null;
        double dst = 100000;
        for (GamePlayer ot : Game.getInstance().getPlayers().values()) {
            if (ot.getTeam() == gp.getTeam() || !isInGame(ot.getPlayer())) {
                continue;
            }
            double cur = wh.distance(ot.getPlayer().getLocation());
            if (cur < dst) {
                dst = cur;
                ret = ot;
            }
        }
        assert ret != null;
        return ret;
    }

    public static Predicate<EntityLiving> getTargetPredicate(int team) {
        return (entity) -> entity != null && isEnemy(entity.getBukkitEntity(), team);
    }
}
