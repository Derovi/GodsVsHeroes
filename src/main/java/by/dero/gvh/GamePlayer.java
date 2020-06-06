package by.dero.gvh;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class GamePlayer {
    private Player player;
    private String className;
    private HashMap<String, Item> items = new HashMap<>();
    private int team;

    public GamePlayer(Player player, String className) {
        this.player = player;
        this.className = className;
    }

    public Item getSelectedItem() {
        ItemStack selectedItem = player.getInventory().getItemInMainHand();
        if (!selectedItem.hasItemMeta()) {
            return null;
        }
        
        return null;
    }

    public void addItem(String name, int level) {
        try {
            ItemDescription itemDescription = Plugin.getInstance().getData().getItems().get(name);
            ItemInfo itemInfo = itemDescription.getLevels().get(level);
            Item item = (Item) Plugin.getInstance().getData().getItemNameToClass().
                    get(name).getConstructor().newInstance();
            items.put(name, item);
            ItemStack itemStack = new ItemStack(itemInfo.getMaterial(), itemInfo.getAmount());
            player.getInventory().setItem(itemDescription.getSlot(), itemStack);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
