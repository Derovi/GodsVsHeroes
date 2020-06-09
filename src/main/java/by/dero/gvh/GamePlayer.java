package by.dero.gvh;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GamePlayer {
    private Player player;
    private String className;
    private HashMap<String, Item> items = new HashMap<>();
    private int team;

    public GamePlayer(Player player, String className) {
        this.player = player;
        selectClass(className);
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
            ItemInfo itemInfo = itemDescription.getLevels().get(level);
            Item item = (Item) Plugin.getInstance().getData().getItemNameToClass().
                    get(name).getConstructor().newInstance();
            items.put(name, item);
            ItemStack itemStack = new ItemStack(itemInfo.getMaterial(), itemInfo.getAmount());
            itemStack.getItemMeta().setDisplayName(itemInfo.getDisplayName());
            itemStack.getItemMeta().setLore(itemInfo.getLore());
            // add tag as last line of lore
            itemStack.getItemMeta().getLore().add(Plugin.getInstance().getData().getItemNameToTag().get(name));
            player.getInventory().setItem(itemDescription.getSlot(), itemStack);
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
