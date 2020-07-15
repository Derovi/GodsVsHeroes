package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ItemInfo {
    static class EnchantInfo {
        public EnchantInfo(final String name, final int level, final boolean visible) {
            this.name = name;
            this.level = level;
            this.visible = visible;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public boolean isVisible() {
            return visible;
        }

        private final String name;
        private final int level;
        private final boolean visible;
    }

    private Material material = null;
    private final List<EnchantInfo> enchantments = new LinkedList<>();
    private String displayName = null;
    private List<String> lore = null;
    private int amount = 1;
    private int cooldown = 5;
    private int cost = 5;
    private ItemDescription description;

    public ItemInfo(ItemDescription description) {
        this.description = description;
    }

    public static void main(String[] args) throws IOException {
    }

    public List<EnchantInfo> getEnchantments() {
        return enchantments;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Material getMaterial() {
        return material == null ? description.getMaterial() : material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName == null ? description.getDisplayName() : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore == null ? description.getLore() : lore;
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

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public ItemDescription getDescription() {
        return description;
    }

    public void setDescription(ItemDescription description) {
        this.description = description;
    }
}
