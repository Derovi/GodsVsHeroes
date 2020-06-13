package by.dero.gvh.model;

import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.util.*;

public class ItemInfo {
    static class EnchantInfo {
        public EnchantInfo(NamespacedKey key, int level, boolean visible) {
            this.key = key;
            this.level = level;
            this.visible = visible;
        }

        public NamespacedKey getKey() {
            return key;
        }

        public int getLevel() {
            return level;
        }

        public boolean isVisible() {
            return visible;
        }

        private NamespacedKey key;
        private int level;
        private boolean visible;
    }

    private Material material = Material.BEDROCK;
    private final List<EnchantInfo> enchantments = new LinkedList<>();
    private String displayName = "§cNot found";
    private List<String> lore = Arrays.asList("First line", "Second line");
    private int amount = 1;

    public static void main(String[] args) throws IOException {
        ItemInfo info = new ItemInfo();
        info.getEnchantments().add(new EnchantInfo(Enchantment.LUCK.getKey(), 7, true));
        info.getEnchantments().add(new EnchantInfo(Enchantment.ARROW_FIRE.getKey(), 4, false));
        String json = ResourceUtils.readResourceFile("/items/arrowrain.json");
        ItemInfo info2 = new Gson().fromJson(json, ItemInfo.class);
        System.out.println(info2.getEnchantments().size());
        /*Data data = new Data(new LocalStorage());
        data.registerItem("flybow", FlyBowInfo.class, FlyBow.class);
        ItemDescription description = new ItemDescription();
        description.setName("flybow");
        description.getLevels().add(new ItemInfo());
        description.getLevels().add(new FlyBowInfo());
        for (ItemInfo info : description.getLevels()) {
            System.out.println(info instanceof FlyBowInfo);
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(ItemDescription.class,
                ItemDescription.getDeserializer(data)).setPrettyPrinting().create();
        String serialized = gson.toJson(description);
        System.out.println(serialized);
        System.out.println("------------------------------------");
        description = gson.fromJson(serialized, ItemDescription.class);
        System.out.println(gson.toJson(description));*/
    }

    public List<EnchantInfo> getEnchantments() {
        return enchantments;
    }

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
