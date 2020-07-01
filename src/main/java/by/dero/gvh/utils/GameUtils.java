package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.StorageInterface;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;

public class GameUtils {
    private static Player lastUsedLightning;
    private static Long lastLightningTime = 0L;
    public static final double eyeHeight = 1.7775;
    public static GamePlayer getPlayer(String name) {
        return Minigame.getInstance().getGame().getPlayers().getOrDefault(name, null);
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer) {
        damage(damage, target, killer, false);
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer, boolean hasDelay) {
        Minigame.getInstance().getGameEvents().getDamageCause().put(target, killer);
        if (!hasDelay) {
            target.setNoDamageTicks(0);
        }
        target.damage(damage, killer);
    }

    public static Entity spawnEntity(final Location loc, final EntityType type) {
        CraftWorld wrld = ((CraftWorld) loc.getWorld());
        net.minecraft.server.v1_12_R1.Entity entity = wrld.createEntity(loc, type.getEntityClass());
        entity.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        wrld.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return entity.getBukkitEntity();
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

    public static boolean isEnemy(final Entity ent, final int team) {
        if (ent instanceof ArmorStand) {
            return false;
        }
        if (!(ent instanceof LivingEntity) || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof Player)) {
            return true;
        }
        return getPlayer(ent.getName()).getTeam() != team;
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
        if (ent instanceof ArmorStand) {
            return false;
        }
        if (!(ent instanceof LivingEntity) || ent.isDead()) {
            return false;
        }
        if (!(ent instanceof Player)) {
            return true;
        }
        return getPlayer(ent.getName()).getTeam() == team;
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
            if (ot.getTeam() == gp.getTeam()) {
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
}
