package by.dero.gvh;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.interfaces.DoubleHanded;
import by.dero.gvh.nmcapi.NMCUtils;
import by.dero.gvh.stats.PlayerStats;
import by.dero.gvh.utils.Board;
import by.dero.gvh.utils.GameUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GamePlayer extends GameObject {
    @Getter @Setter
    private Player player;
    @Getter @Setter
    private String className = "default";
    @Getter @Setter
    private PlayerInfo playerInfo;
    private final HashMap<String, Item> items = new HashMap<>();
    @Getter @Setter
    private Item lastUsed;
    @Getter @Setter
    private boolean actionBarBlocked = false;
    @Getter @Setter
    private ItemStack[] contents = null;
    @Getter @Setter
    private boolean disabled = false;
    @Getter @Setter
    private boolean ultimateBuf = false;
    @Getter @Setter
    private boolean lockedTitles = false;
    @Getter @Setter
    private PlayerStats playerStats;

    @Getter private Board board;

    public GamePlayer(Player player) {
        super(player);
        this.player = player;
        this.playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
        this.playerStats = Plugin.getInstance().getGameStatsData().getPlayerStats(player.getName());
    }
    
    public Item getSelectedItem() {
        ItemStack selectedItem = player.getInventory().getItemInMainHand();
        return items.getOrDefault(NMCUtils.getNBT(selectedItem).getString("custom"), null);
    }

    public void selectClass(String className) {
        player.getInventory().clear();
        this.className = className;
    }

    public void addItem(String name, int level) {
        try {
            Item item = Plugin.getInstance().getData().getItemNameToClass().
                    get(name).getConstructor(String.class, int.class, Player.class).newInstance(name, level, player);
            items.put(name, item);
            if (!item.getDescription().isInvisible()) {
                int slot = Plugin.getInstance().getData().getItems().get(item.getName()).getSlot();
                if (slot > 0) {
                    player.getInventory().setItem(slot, item.getItemStack());
                } else if (slot == -1) {
                    player.getInventory().setHelmet(item.getItemStack());
                } else if (slot == -2) {
                    player.getInventory().setChestplate(item.getItemStack());
                } else if (slot == -3) {
                    player.getInventory().setLeggings(item.getItemStack());
                } else if (slot == -4) {
                    player.getInventory().setBoots(item.getItemStack());
                } else {
                    HashMap<String, Integer> order = playerInfo.getItemsOrder().getOrDefault(className, null);
                    if (GameUtils.goodOrder(order, className)) {
                        player.getInventory().setItem(order.get(name), item.getItemStack());
                    } else {
                        player.getInventory().addItem(item.getItemStack());
                    }
                }
                if (item instanceof DoubleHanded) {
                    player.getInventory().setItemInOffHand(item.getItemStack());
                }
            }
            charges.putIfAbsent(item.getName(), item.getInfo().getAmount());
        } catch (Exception ex) {
            System.err.println("Can't add item! " + name + ":" + level + " to " + getPlayer().getName());
            ex.printStackTrace();
        }
    }

    public void showInventory() {
        if (!isInventoryHided()) {
            return;
        }
        player.getInventory().setContents(contents);
        contents = null;
        updateInventory();
    }

    public void hideInventory() {
        if (isInventoryHided()) {
            return;
        }
        contents = player.getInventory().getContents().clone();
        player.getInventory().clear();
    }

    private final HashMap<String, Integer> charges = new HashMap<>();
    private final HashMap<Item, Integer> itemsSlots = new HashMap<>();

    public boolean isCharged(String item) {
        return charges.getOrDefault(item, 1) > 0;
    }
    
    public boolean consume(final Item item) {
        Player player = getPlayer();

        int cur = charges.get(item.getName());
        if (cur <= 0) {
            return false;
        }

        charges.put(item.getName(), cur - 1);
        if (!item.getDescription().isInvisible()) {
            int slot = player.getInventory().getHeldItemSlot();
            itemsSlots.put(item, slot);
            updateSlot(item, slot);
        }
        replenish(item);
        return true;
    }

    private void replenish(final Item item) {
        Player player = getPlayer();
        HashMap<String, Integer> localCharges = charges;
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
                    updateSlot(item, slot);
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

    private void updateSlot(Item item, int slot) {
        if (isInventoryHided()) {
            return;
        }
        Player player = getPlayer();

        int need = charges.get(item.getName());

        PlayerInventory inv = player.getInventory();
        need = Math.max(need, 1);
        if (need == 0) {
//            inv.setItem(slot, Item.getPane(item.getInfo().getDisplayName()));
        } else if (inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE) ||
                inv.getItem(slot).getAmount() != need) {
            ItemStack zxc = item.getItemStack();
            zxc.setAmount(need);
            inv.setItem(slot, zxc);
        }
    }

    public void updateInventory() {
        if (isInventoryHided()) {
            return;
        }
        Player player = getPlayer();
        PlayerInventory inv = player.getInventory();

        for (Map.Entry<Item, Integer> obj : itemsSlots.entrySet()) {
            int slot = obj.getValue();
            int need = charges.get(obj.getKey().getName());
            need = Math.max(need, 1);
            if (need == 0) {
                inv.setItem(slot, Item.getPane(obj.getKey().getInfo().getDisplayName()));
            } else if (inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.STAINED_GLASS_PANE) ||
                        inv.getItem(slot).getAmount() != need) {
                ItemStack zxc = obj.getKey().getItemStack();
                zxc.setAmount(need);
                inv.setItem(slot, zxc);
            }
        }
    }

    public boolean isInventoryHided() {
        return contents != null;
    }

    public HashMap<String, Item> getItems() {
        return items;
    }

    public void setBoard(final Board board) {
        if (this.board != null) {
            this.board.clear();
        }
        getPlayer().setScoreboard(board.getScoreboard());
        this.board = board;
    }

    public boolean setPreferredTeam (int preferredTeam) {
        if (preferredTeam == getTeam()) {
            return true;
        }
        int[] cnt = new int[Game.getInstance().getInfo().getTeamCount()];
        if (preferredTeam >= cnt.length) {
            return false;
        }
        for (GamePlayer gp : Game.getInstance().getPlayers().values()) {
            if (gp.getTeam() != -1) {
                cnt[gp.getTeam()]++;
            }
        }
        if (getTeam() != -1) {
            cnt[getTeam()]--;
        }
        int mx1 = -1, val1 = -1, val2 = -1;
        for (int i = 0; i < cnt.length; i++) {
            if (val1 <= cnt[i]) {
                val2 = val1;
                mx1 = i;
                val1 = cnt[i];
            } else if (val2 <= cnt[i]) {
                val2 = cnt[i];
            }
        }

        if (preferredTeam == mx1 && val1 - val2 >= 1 && val1 >= Game.getInstance().getPlayers().size() / 2) {
            return false;
        }
        setTeam(preferredTeam);
        return true;
    }
}
