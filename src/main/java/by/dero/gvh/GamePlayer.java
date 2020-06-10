package by.dero.gvh;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class GamePlayer {
    private Player player;
    private String className;
    private HashMap<String, Item> items = new HashMap<>();
    private int team;

    public GamePlayer(Player player) {
        this.player = player;
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
        items.clear();
        player.getInventory().clear();
        UnitClassDescription classDescription = Plugin.getInstance().getData().getUnits().get(className);
        for (String itemName : classDescription.getItemNames()) {
            addItem(itemName, 0);
        }
    }

    public void addItem(String name, int level) {
        try {
            ItemDescription itemDescription = Plugin.getInstance().getData().getItems().get(name);
            Item item = (Item) Plugin.getInstance().getData().getItemNameToClass().
                    get(name).getConstructor(String.class, int.class, Player.class).newInstance(name, level, player);
            items.put(name, item);
            player.getInventory().setItem(itemDescription.getSlot(), item.getItemStack());
        } catch (Exception ex) {
            System.err.println("Can't add item! " + name + ":" + String.valueOf(level) + " to " + getPlayer().getName());
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
