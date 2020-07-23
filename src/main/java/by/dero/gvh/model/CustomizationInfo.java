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
public class CustomizationInfo {
    public interface ItemStackCustomizer {
        void customize(ItemStack itemStack);
    }

    @Getter
    @Setter
    ItemStackCustomizer customizer = null;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int groupID;

    @Getter
    @Setter
    private String hero = "all";

    @Getter
    @Setter
    private String displayName = "Empty";

    @Getter
    @Setter
    private Material material = Material.BEDROCK;

    @Getter
    @Setter
    private List<String> description = new ArrayList<>();

    @Getter
    @Setter
    private int cost = 0;

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>(description);
        lore.add("");
        lore.add("§fЦена: §b%cost% кристалликов".replace("%cost%", Integer.toString(cost)));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        if (customizer != null) {
            customizer.customize(itemStack);
        }
        return itemStack;
    }
}
