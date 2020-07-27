package by.dero.gvh.model;

import by.dero.gvh.nmcapi.NMCUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
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

    @AllArgsConstructor
    public static class NBT {
        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String value;
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

    @Getter
    @Setter
    @Builder.Default
    private NBT nbt = null;

    public void addNBT(ItemStack itemStack) {
        itemStack.setType(material);
        NBTTagCompound compound = NMCUtils.getNBT(itemStack);
        compound.set(nbt.getName(), new NBTTagString(nbt.getValue()));
        NMCUtils.setNBT(itemStack, compound);
    }

    public ItemStack getItemStack() {
        return getItemStack(false);
    }

    public ItemStack getItemStack(boolean addCost) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
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
        if (nbt != null) {
            NBTTagCompound compound = NMCUtils.getNBT(itemStack);
            compound.set(nbt.getName(), new NBTTagString(nbt.getValue()));
            NMCUtils.setNBT(itemStack, compound);
        }
        return itemStack;
    }
}
