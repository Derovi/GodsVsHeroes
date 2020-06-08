package by.dero.gvh.model;

import by.dero.gvh.model.items.FlyBow;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ItemInfo {
    private Material material = Material.BEDROCK;
    private String displayName = "§cNot found";
    private List<String> lore = Arrays.asList("First line", "Second line");
    private int amount = 1;

    public static void main(String[] args) {
        Data data = new Data(new LocalStorage());
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
        System.out.println(gson.toJson(description));
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
