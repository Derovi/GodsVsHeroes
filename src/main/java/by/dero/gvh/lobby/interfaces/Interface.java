package by.dero.gvh.lobby.interfaces;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Interface {
    @Getter private final Player player;
    @Getter @Setter
    private Inventory inventory;
    private final Runnable[] buttonActions;
    @Getter private final InterfaceManager manager;
    @Getter private final int height;
    @Setter protected Runnable onBackButton = null;

    public Interface(InterfaceManager manager, Player player, int height, String name) {
        this.manager = manager;
        this.height = height;
        this.player = player;
        inventory = Bukkit.createInventory(null, 9 * height, name);
        buttonActions = new Runnable[9 * height];
    }

    public void open() {
        player.openInventory(inventory);
        manager.register(player.getName(), this);
    }

    public void onInventoryClosed() {}

    public void update() {
        player.updateInventory();
    }

    public void close() {
        player.closeInventory();
    }

    public void clear() {
        for (int x = 0; x < 9; ++x) {
            for (int y = 0; y < height; ++y) {
                removeButton(x, y);
            }
        }
        //update();
    }

    public void clicked(int pos) {
        if (pos < 0) {
            return;
        }
        if (buttonActions[pos] != null) {
            buttonActions[pos].run();
        }
    }

    public void addItem(int x, int y, ItemStack itemStack) {
        int pos = getPos(x, y);
        buttonActions[pos] = null;
        inventory.setItem(pos, itemStack);
    }

    public void addButton(int x, int y, ItemStack itemStack, Runnable onClick) {
        int pos = getPos(x, y);
        buttonActions[pos] = onClick;
        inventory.setItem(pos, itemStack);
    }

    public void removeButton(int x, int y) {
        int pos = getPos(x, y);
        buttonActions[pos] = null;
        inventory.setItem(pos, new ItemStack(Material.AIR));
    }

    protected int getPos(int x, int y) {
        return (height - y - 1) * 9 + x;
    }
    
    public void onSlotClicked(InventoryClickEvent event) {
    
    }
}
