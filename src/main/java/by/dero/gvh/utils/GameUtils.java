package by.dero.gvh.utils;

import by.dero.gvh.GameMob;
import by.dero.gvh.GameObject;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class GameUtils {
    public static final double eyeHeight = 1.7775;
    public static final Vector zeroVelocity = new Vector(0, -0.0784000015258789, 0);
    
    public static HashMap<Character, Byte> codeToData = null;
    public static final ItemStack clearItem = new ItemStack(Material.AIR);
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

    public static String getTeamColor(int team) {
        return Lang.get("commands." + (char)('1' + team)).substring(0, 2);
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

    public static GameObject getObject(LivingEntity entity) {
        return entity instanceof Player ? getPlayer(entity.getName()) : getMob(entity.getUniqueId());
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer) {
        damage(damage, target, killer, false);
    }

    public static <T> ArrayList<T> selectItems(GamePlayer gp, Class<T> cl) {
        final ArrayList<T> list = new ArrayList<>();
        for (Item item : gp.getItems().values()) {
            if (cl.isInstance(item)) {
                list.add(cl.cast(item));
            }
        }
        return list;
    }

    public static void damage(double damage, LivingEntity target, LivingEntity killer, boolean hasDelay) {
        if (!hasDelay) {
            target.setNoDamageTicks(0);
        }
        if (target.equals(killer)) {
            target.damage(damage);
        } else {
            target.damage(damage, killer);
        }
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
        GamePlayer player = item.getOwnerGP();
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
    
    public static void stunMessage(GamePlayer player, int duration) {
        player.setActionBarBlocked(true);
        MessagingUtils.sendSubtitle(Lang.get("game.stunMessage"), player.getPlayer(), 0, duration, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setActionBarBlocked(false);
            }
        }.runTaskLater(Plugin.getInstance(), duration);
    
    }

    public static ArrayList<GameObject> getGameObjects() {
        ArrayList<GameObject> list = new ArrayList<>();
        for (Map.Entry<UUID, GameMob> obj : Game.getInstance().getMobs().entrySet()) {
            if (!obj.getValue().getEntity().isDead()) {
                list.add(obj.getValue());
            }
        }
        list.addAll(Game.getInstance().getPlayers().values());
        return list;
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
        return player != null && player.isOnline();
    }

    public static List<LivingEntity> getNearby(final Location wh, final double radius) {
        final List<LivingEntity> buf = new ArrayList<>();
        for (Entity ent : Objects.requireNonNull(wh.getWorld()).getNearbyEntities(wh, radius, radius, radius)) {
            if (ent instanceof LivingEntity && ent.getLocation().distance(wh) <= radius && !isDeadPlayer(ent)) {
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

    public static LivingEntity getTargetEntity(final Player entity, final double maxRange,
                                               java.util.function.Predicate<LivingEntity> pred) {
        return getTarget(entity, entity.getWorld().getLivingEntities(), maxRange, 1, pred);
    }
    
    public static LivingEntity getTargetEntity(final Player entity, final double maxRange, final double prec,
                                               java.util.function.Predicate<LivingEntity> pred) {
        return getTarget(entity, entity.getWorld().getLivingEntities(), maxRange, prec, pred);
    }
    
    public static void changeEquipment(Player player, int slot, int duration, ItemStack item) {
        final ItemStack saved;
        final PlayerInventory inv = player.getInventory();
        switch (slot) {
            case -1 : saved = inv.getHelmet(); inv.setHelmet(item); break;
            case -2 : saved = inv.getChestplate(); inv.setChestplate(item); break;
            case -3 : saved = inv.getLeggings(); inv.setLeggings(item); break;
            case -4 : saved = inv.getBoots(); inv.setBoots(item); break;
            default : saved = inv.getItem(slot); inv.setItem(slot, item); break;
        }
        SafeRunnable restoreInv = new SafeRunnable() {
            int timeRes = 0;
            @Override
            public void run() {
                if (GameUtils.isDeadPlayer(player)) {
                    this.cancel();
                    return;
                }
                if (timeRes > duration) {
                    this.cancel();
                    switch (slot) {
                        case -1 : inv.setHelmet(saved); break;
                        case -2 : inv.setChestplate(saved); break;
                        case -3 : inv.setLeggings(saved); break;
                        case -4 : inv.setBoots(saved); break;
                        default : inv.setItem(slot, saved); break;
                    }
                    return;
                }
                timeRes += 5;
            }
        };
        restoreInv.runTaskTimer(Plugin.getInstance(), 0, 5);
        Game.getInstance().getRunnables().add(restoreInv);
    }
    
    public static <T extends LivingEntity> T getTarget(Player entity, Iterable<T> entities, double maxRange, double prec,
                                                       java.util.function.Predicate<LivingEntity> pred) {
        if (entity == null)
            return null;
        T target = null;
        for (final T other : entities) {
            Location otLoc = other.getLocation().add(0, other.getHeight() / 2, 0);
            if (otLoc.distance(entity.getEyeLocation()) > maxRange || !pred.test(other)) {
                continue;
            }
            final Vector n = otLoc.toVector().subtract(entity.getEyeLocation().toVector());
            if (entity.getEyeLocation().getDirection().normalize().crossProduct(n).lengthSquared() <
                    entity.getHeight() * entity.getHeight() / 4 * prec * prec &&
                    n.normalize().dot(entity.getEyeLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().add(0, target.getHeight() / 2, 0).
                        distanceSquared(entity.getEyeLocation()) > otLoc.distanceSquared(entity.getEyeLocation())) {
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

    public static GamePlayer getNearestEnemyPlayer(GameObject gp) {
        Location wh = gp.getEntity().getLocation();
        GamePlayer ret = null;
        double dst = 10000;
        for (GamePlayer ot : Game.getInstance().getPlayers().values()) {
            if (ot.getTeam() == gp.getTeam() || !isInGame(ot.getPlayer()) || isDeadPlayer(ot.getPlayer())) {
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
    
    
    public static GameObject getNearestEnemy(GameObject gp) {
        Location wh = gp.getEntity().getLocation();
        GameObject ret = null;
        double dst = 10000;
        for (GameObject ot : getGameObjects()) {
            if (ot.getTeam() == gp.getTeam() || isDeadPlayer(ot.getEntity())) {
                continue;
            }
            double cur = wh.distance(ot.getEntity().getLocation());
            if (cur < dst) {
                dst = cur;
                ret = ot;
            }
        }
        return ret;
    }

    public static ItemStack getHead(Player player) {
        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(Lang.get("interfaces.stats"));
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
        skull.setItemMeta(skullMeta);
        return skull;
    }
    
    public static Predicate<EntityLiving> getTargetPredicate(int team) {
        return (entity) -> entity != null && isEnemy(entity.getBukkitEntity(), team);
    }

    public static Predicate<EntityPlayer> getTargetPlayerPredicate(int team) {
        return (entityPlayer -> entityPlayer != null && isEnemy(entityPlayer.getBukkitEntity(), team));
    }
}
