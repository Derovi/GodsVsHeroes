package by.dero.gvh.minigame;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.GameUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class LootsManager implements Listener {
    private final long cooldown = 400;
    private final ArrayList<LootsNode> loots = new ArrayList<>();
    private boolean loaded = false;

    public class LootsNode {
        private final CraftArmorStand stand;
        private final PotionEffect effect;
        private final FlyingText text;
        private final String updateText;
        private final Location loc;
        private Long lastUsed = 0L;

        private final ArrayList<Integer> updateIdxs = new ArrayList<>();
        public void update(String... args) {
            long left = lastUsed + cooldown * 50 - System.currentTimeMillis();
            if (left >= 0) {
                text.setText(Lang.get("loots.labelCooldown").replace("%time%", String.valueOf(left / 1000)));
                return;
            }
            String result = updateText;
            for (int i = 0; i < updateIdxs.size(); i += 2) {
                result = result.replace(result.substring(updateIdxs.get(i), updateIdxs.get(i+1)+1), args[i / 2]);
            }
            text.setText(result);
        }

        public LootsNode(Location loc, PotionEffect effect, String headName, String text) {
            this.updateText = text;
            this.loc = loc.clone();
            this.text = new FlyingText(loc.clone().add(0, 1.5, 0), "");
            this.effect = effect;
            EntityArmorStand ent = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
                    loc.x, loc.y - GameUtils.eyeHeight + 0.3, loc.z);
            stand = (CraftArmorStand) ent.getBukkitEntity();
            stand.setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
            GameUtils.setInvisibleFlags(stand);
            ent.getWorld().addEntity(ent, CreatureSpawnEvent.SpawnReason.CUSTOM);
            ent.setCustomNameVisible(false);
            stand.getEquipment().setHelmet(getHead(headName));

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '%') {
                    updateIdxs.add(i);
                }
            }
            update();
        }

        public boolean isReady() {
            return lastUsed + cooldown * 50 - System.currentTimeMillis() < 0;
        }

        public void use(Player player) {
            player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, null);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.07f, 1);
            GameUtils.getPlayer(player.getName()).addEffect(effect);
            lastUsed = System.currentTimeMillis();
            stand.teleport(stand.getLocation().subtract(0, 30, 0));
            final BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    stand.teleport(stand.getLocation().clone().add(0,30,0));
                }
            };
            runnable.runTaskLater(Plugin.getInstance(), cooldown);
            Game.getInstance().getRunnables().add(runnable);
        }

        public void unload() {
            stand.remove();
            text.unload();
        }

        public Location getLoc () {
            return loc;
        }
    }

    public void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (LootsNode node : loots) {
                    node.update();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 10);
        Game.getInstance().getRunnables().add(runnable);

        GameInfo info = Game.getInstance().getInfo();
        for (final DirectedPosition pos : info.getHealPoints()) {
            loots.add(new LootsNode(pos.toLocation(info.getLobbyWorld()), new PotionEffect(PotionEffectType.HEAL, 1, 10),
                    "heal", Lang.get("loots.labelHeal").replace("%pts%",  "10")));
        }
        for (final DirectedPosition pos : info.getSpeedPoints()) {
            loots.add(new LootsNode(pos.toLocation(info.getLobbyWorld()), new PotionEffect(PotionEffectType.SPEED, 240, 2),
                    "speed", Lang.get("loots.labelReady")));
        }
        for (final DirectedPosition pos : info.getResistancePoints()) {
            loots.add(new LootsNode(pos.toLocation(info.getLobbyWorld()), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 240, 1),
                    "resistance", Lang.get("loots.labelReady")));
        }
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


    public static ItemStack createSkull(String url, String name) {
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
        if (!loaded) {
            return;
        }
        loaded = false;
        for (LootsNode node : loots) {
            node.unload();
        }
    }

    @EventHandler
    public void checkForLoots(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
            for (LootsNode node : loots) {
                if (node.isReady() && node.loc.distance(player.getLocation()) <= 1) {
                    node.use(player);
                }
            }
        }
    }
}
