package by.dero.gvh;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ChargesManager {
    final HashMap<UUID, HashMap<String, Integer> > charges = new HashMap<>();
    private static ChargesManager instance;
    public ChargesManager() {
        instance = this;
        for (final GamePlayer gp : Game.getInstance().getPlayers().values()) {
            final UUID uuid = gp.getPlayer().getUniqueId();
            charges.put(uuid, new HashMap<>());
            for (final Item item : gp.getItems().values()) {
                charges.get(uuid).put(item.getName(), item.getInfo().getAmount());
            }
        }
    }

    public static ChargesManager getInstance() {
        return instance;
    }

    public boolean consume(final Player player, final Item item) {
        final UUID uuid = player.getUniqueId();
        final int cur = charges.get(uuid).get(item.getName());
        if (cur == 0) {
            return false;
        }
        if (item.getDescription().isInvisible()) {
            replenishInvisible(player, item);
        } else {
            final int slot = player.getInventory().getHeldItemSlot();
            if (cur == 1) {
                final ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
                pane.setAmount(1);
                final ItemMeta meta = pane.getItemMeta();
                meta.setDisplayName(item.getInfo().getDisplayName());
                pane.setItemMeta(meta);
                player.getInventory().setItem(slot, pane);
            } else {
                player.getInventory().getItem(slot).setAmount(cur - 1);
            }
            replenishVisible(player, item);
        }
        charges.get(uuid).put(item.getName(), cur - 1);
        return true;
    }

    private void replenishInvisible(final Player player, final Item item) {
        final UUID uuid = player.getUniqueId();
        final int need = item.getInfo().getAmount();
        if (need != charges.get(uuid).get(item.getName())) {
            return;
        }

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                final int cur = charges.get(uuid).get(item.getName());
                charges.get(uuid).put(item.getName(), cur + 1);
                if (cur + 1 == need) {
                    this.cancel();
                }
            }
        };

        final long cd = item.getCooldown().getDuration();
        runnable.runTaskTimer(Plugin.getInstance(), cd, cd);
        Game.getInstance().getRunnables().add(runnable);
    }

    private void replenishVisible(final Player player, final Item item) {
        final UUID uuid = player.getUniqueId();
        if (item.getInfo().getAmount() != charges.get(uuid).get(item.getName())) {
            return;
        }

        final BukkitRunnable runnable = new BukkitRunnable() {
            final PlayerInventory inv = player.getInventory();
            final int slot = inv.getHeldItemSlot();
            final int need = item.getInfo().getAmount();
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                final int cur = charges.get(uuid).get(item.getName());
                if (inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
                    inv.setItem(slot, item.getItemStack());
                }
                inv.getItem(slot).setAmount(cur + 1);

                charges.get(uuid).put(item.getName(), cur + 1);
                if (cur + 1 == need) {
                    this.cancel();
                }
            }
        };
        final long cd = item.getCooldown().getDuration();
        runnable.runTaskTimer(Plugin.getInstance(), cd, cd);
        Game.getInstance().getRunnables().add(runnable);
    }
}