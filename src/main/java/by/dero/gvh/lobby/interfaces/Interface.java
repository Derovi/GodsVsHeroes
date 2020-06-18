package by.dero.gvh.lobby.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Interface {
    private final Player player;
    private Inventory inventory;
    private final Runnable[] buttonActions;

    public Interface(InterfaceManager manager, Player player, int height, String name) {
        inventory = Bukkit.createInventory(null, 9 * height, name);
        buttonActions = new Runnable[9 * height];
        this.player = player;
        manager.register(player.getName(), this);
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
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
        int pos = y * 9 + x;
        buttonActions[pos] = null;
        inventory.setItem(pos, itemStack);
    }

    public void addButton(int x, int y, ItemStack itemStack, Runnable onClick) {
        int pos = y * 9 + x;
        buttonActions[pos] = onClick;
        inventory.setItem(pos, itemStack);
    }

    public void removeButton(int x, int y) {
        int pos = y * 9 + x;
        buttonActions[pos] = null;
        inventory.setItem(pos, new ItemStack(Material.AIR));
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
