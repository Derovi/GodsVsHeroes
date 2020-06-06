package by.dero.gvh.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Item {
    private Material material;
    private String name;
    private String displayName;
    private List<String> lore;
    private int level;
    private int slot;
    private int amount;

    public Item() {}

    public void give(Player player) {
        player.getInventory().setItem(slot, getItemStack());
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.getItemMeta().setDisplayName(displayName);
        itemStack.getItemMeta().setLore(lore);
        return itemStack;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
