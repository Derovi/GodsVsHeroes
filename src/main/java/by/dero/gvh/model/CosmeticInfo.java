package by.dero.gvh.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Builder
public class CosmeticInfo {
    public interface ItemStackCustomizer {
        void customize(ItemStack itemStack);
    }

    public enum Rarity {
        UNCOMMON, RARE, MYTHICAL, LEGENDARY, IMMORTAL;

        public String getName() {
            return Lang.get("rarity." + toString().toLowerCase());
        }
    }

    @Getter
    @Setter
    @Builder.Default
    ItemStackCustomizer customizer = null;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int groupID;

    @Getter
    @Setter
    @Builder.Default
    private String hero = "all";

    @Getter
    @Setter
    @Builder.Default
    private String displayName = "Empty";

    @Getter
    @Setter
    @Builder.Default
    private Material material = Material.BEDROCK;

    @Getter
    @Setter
    @Builder.Default
    private List<String> description = new ArrayList<>();

    @Getter
    @Setter
    @Builder.Default
    private int cost = 0;

    @Getter
    @Setter
    @Builder.Default
    private Rarity rarity = Rarity.UNCOMMON;

    public ItemStack getItemStack() {
        return getItemStack(false);
    }

    public ItemStack getItemStack(boolean addCost) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>(description);
        lore.add("");
        lore.add("§fРедкость: " + rarity.getName());
        if (addCost) {
            lore.add("§fЦена: §b%cost% кристалликов".replace("%cost%", Integer.toString(cost)));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        if (customizer != null) {
            customizer.customize(itemStack);
        }
        return itemStack;
    }
}
