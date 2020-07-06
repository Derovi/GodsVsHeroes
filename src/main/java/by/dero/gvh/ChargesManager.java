package by.dero.gvh;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChargesManager {
    private final HashMap<UUID, HashMap<String, Integer> > charges = new HashMap<>();
    private final HashMap<UUID, HashMap<Item, Integer> > items = new HashMap<>();

    private static ChargesManager instance;
    public ChargesManager() {
        instance = this;
        for (final GamePlayer gp : Game.getInstance().getPlayers().values()) {
            final UUID uuid = gp.getPlayer().getUniqueId();
            charges.put(uuid, new HashMap<>());
            for (final Item item : gp.getItems().values()) {
                charges.get(uuid).put(item.getName(), item.getInfo().getAmount());
            }
            items.put(uuid, new HashMap<>());
        }
    }

    public static ChargesManager getInstance() {
        return instance;
    }

    public boolean consume(final GamePlayer gp, final Item item) {
        Player player = gp.getPlayer();
        UUID uuid = player.getUniqueId();

        HashMap<String, Integer> localCharges = charges.get(uuid);

        int cur = localCharges.get(item.getName());
        if (cur <= 0) {
            return false;
        }

        int slot = player.getInventory().getHeldItemSlot();
        items.get(uuid).put(item, slot);

        localCharges.put(item.getName(), cur - 1);
        updateSlot(gp, item, slot);
        replenish(gp, item);
        return true;
    }

    private void replenish(final GamePlayer gp, final Item item) {
        Player player = gp.getPlayer();
        UUID uuid = player.getUniqueId();
        HashMap<String, Integer> localCharges = charges.get(uuid);
        int need = item.getInfo().getAmount();
        boolean visible = !item.getDescription().isInvisible();

        if (need != localCharges.get(item.getName()) + 1) {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!GameUtils.isInGame(player)) {
                    this.cancel();
                    return;
                }
                int cur = localCharges.get(item.getName()) + 1;
                localCharges.put(item.getName(), cur);
                if (visible) {
                    updateSlot(gp, item, slot);
                }
                if (need == cur) {
                    this.cancel();
                }
            }
        };

        final long cd = item.getCooldown().getDuration();
        runnable.runTaskTimer(Plugin.getInstance(), cd, cd);
        Game.getInstance().getRunnables().add(runnable);
    }

    public boolean addItem(final GamePlayer gp, final Item item, final int slot) {
        Player player = gp.getPlayer();
        UUID uuid = player.getUniqueId();

        HashMap<String, Integer> localCharges = charges.get(player.getUniqueId());
        int cur = localCharges.get(item.getName());

        if (!GameUtils.isInGame(player) || cur >= item.getInfo().getAmount()) {
            return false;
        } else {
            localCharges.put(item.getName(), cur + 1);
            if (!item.getDescription().isInvisible()) {
                updateSlot(gp, item, slot);
            }
        }
        return true;
    }

    public int getCharges(final Player player, final Item item) {
        return charges.get(player.getUniqueId()).get(item.getName());
    }

    private void updateSlot(GamePlayer gp, Item item, int slot) {
        if (gp.isInventoryHided()) {
            return;
        }
        Player player = gp.getPlayer();

        int need = charges.get(player.getUniqueId()).get(item.getName());

        PlayerInventory inv = player.getInventory();
        if (need == 0) {
            inv.setItem(slot, Item.getPane(item.getInfo().getDisplayName()));
        } else if (inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
            inv.setItem(slot, item.getItemStack());
            inv.getItem(slot).setAmount(need);
        } else if (inv.getItem(slot).getAmount() != need) {
            inv.getItem(slot).setAmount(need);
        }
    }

    public void updateInventory(final GamePlayer gp) {
        if (gp.isInventoryHided()) {
            return;
        }
        Player player = gp.getPlayer();
        HashMap<String, Integer> localCharges = charges.get(player.getUniqueId());
        PlayerInventory inv = player.getInventory();

        for (Map.Entry<Item, Integer> obj : items.get(player.getUniqueId()).entrySet()) {
            int slot = obj.getValue();
            int need = localCharges.get(obj.getKey().getName());
            if (need == 0) {
                inv.setItem(slot, Item.getPane(obj.getKey().getInfo().getDisplayName()));
            } else if (inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
                inv.setItem(slot, obj.getKey().getItemStack());
                inv.getItem(slot).setAmount(need);
            } else if (inv.getItem(slot).getAmount() != need) {
                inv.getItem(slot).setAmount(need);
            }
        }
    }
}
