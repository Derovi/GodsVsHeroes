package by.dero.gvh;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
public class GamePlayer {
    private Player player;
    private String className = "default";
    private PlayerInfo playerInfo;
    private final HashMap<String, Item> items = new HashMap<>();
    private int team;

    public GamePlayer(Player player) {
        this.player = player;
        this.playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
    }

    public Item getSelectedItem() {
        ItemStack selectedItem = player.getInventory().getItemInMainHand();
        if (!selectedItem.hasItemMeta()) {
            return null;
        }
        if (!selectedItem.getItemMeta().hasLore() || selectedItem.getItemMeta().getLore().isEmpty()) {
            return  null;
        }
        String tag = selectedItem.getItemMeta().getLore().get(selectedItem.getItemMeta().getLore().size() - 1);
        String itemName = Plugin.getInstance().getData().getTagToItemName().get(tag);
        return items.getOrDefault(itemName, null);
    }

    public void selectClass(String className) {
        this.className = className;
    }

    public void addItem(String name, int level) {
        try {
            Item item = (Item) Plugin.getInstance().getData().getItemNameToClass().
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
                    player.getInventory().addItem(item.getItemStack());
                }
            }
        } catch (Exception ex) {
            System.err.println("Can't add item! " + name + ":" + String.valueOf(level) + " to " + getPlayer().getName());
            ex.printStackTrace();
        }
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public HashMap<String, Item> getItems() {
        return items;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
