package by.dero.gvh.minigame;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.GameUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class LootsManager implements Listener {
    private final long cooldown = 1200;
    private final HashMap<String, ArrayList<ArmorStand>> loots = new HashMap<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, FlyingText> texts = new HashMap<>();
    private final HashMap<String, PotionEffect> effects = new HashMap<>();

    public LootsManager() {
        effects.put("heal", new PotionEffect(PotionEffectType.HEAL, 1, 10));
        effects.put("speed", new PotionEffect(PotionEffectType.SPEED, 600, 2));
        effects.put("resistance", new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1));
    }

    public void load() {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                texts.forEach((uuid, text) -> {
                    final long left = cooldowns.getOrDefault(uuid, 0L) +
                            cooldown * 50 - System.currentTimeMillis();
                    if (left >= 0) {
                        text.setText(Lang.get("game.lootsLabelCooldown").replace("%time%", String.valueOf(left / 1000)));
                    } else {
                        text.setText(Lang.get("game.lootsLabelReady"));
                    }
                });
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 10);
        Game.getInstance().getRunnables().add(runnable);

        GameInfo info = Game.getInstance().getInfo();
        for (final DirectedPosition pos : info.getHealPoints()) {
            spawn(pos.toLocation(info.getWorld()), "heal");
        }
        for (final DirectedPosition pos : info.getSpeedPoints()) {
            spawn(pos.toLocation(info.getWorld()), "speed");
        }
        for (final DirectedPosition pos : info.getResistancePoints()) {
            spawn(pos.toLocation(info.getWorld()), "resistance");
        }
    }

    public void spawn(final Location at, final String name) {
        CraftArmorStand stand = (CraftArmorStand) at.getWorld().spawnEntity(
                at.subtract(0, GameUtils.eyeHeight - 0.4, 0), EntityType.ARMOR_STAND);
        GameUtils.setInvisibleFlags(stand);
        stand.getHandle().setCustomNameVisible(false);
        stand.getEquipment().setHelmet(getHead(name));
        if (!loots.containsKey(name)) {
            loots.put(name, new ArrayList<>());
        }
        texts.put(stand.getUniqueId(), new FlyingText(stand.getEyeLocation().add(0,1,0), ""));
        loots.get(name).add(stand);
    }

    public static ItemStack getHead(String name)
    {
        for (Heads head : Heads.values())
        {
            if (head.getName().equalsIgnoreCase(name))
            {
                return head.getItemStack();
            }
        }
        return null;
    }

    private boolean useByName(final String name, final LivingEntity entity) {
        if (!effects.containsKey(name)) {
            return false;
        }
        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 24, 1);
        entity.addPotionEffect(effects.get(name), true);
        return true;
    }

    public static ItemStack createSkull(String url, String name)
    {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try
        {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        }
        catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error)
        {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public void unload() {
        for (final ArrayList<ArmorStand> list : loots.values()) {
            for (final ArmorStand stand : list) {
                stand.remove();
            }
            list.clear();
        }
        texts.forEach((uuid, text) -> text.unload());
        loots.clear();
        cooldowns.clear();

    }

    @EventHandler
    public void checkForLoots(final PlayerMoveEvent event) {
        loots.forEach((name, list) -> list.forEach((loot) -> {
            if ((System.currentTimeMillis() - cooldowns.getOrDefault(loot.getUniqueId(), 0L)) / 50 >= cooldown &&
                    loot.getEyeLocation().distance(event.getPlayer().getLocation()) <= 1) {
                if (useByName(name, event.getPlayer())) {
                    cooldowns.put(loot.getUniqueId(), System.currentTimeMillis());
                    loot.teleport(loot.getLocation().clone().subtract(0,30,0));
                    final BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            loot.teleport(loot.getLocation().clone().add(0,30,0));
                        }
                    };
                    runnable.runTaskLater(Plugin.getInstance(), cooldown);
                    Game.getInstance().getRunnables().add(runnable);
                }
            }
        }));
    }
}
