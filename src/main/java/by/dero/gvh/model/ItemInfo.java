package by.dero.gvh.model;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ItemInfo {
    private Material material = Material.BEDROCK;
    private String displayName = "§cNot found";
    private List<String> lore = Arrays.asList("First line", "Second line");
    private int amount = 1;

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
