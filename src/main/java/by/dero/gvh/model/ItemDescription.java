package by.dero.gvh.model;

import by.dero.gvh.model.itemsinfo.SwordInfo;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ItemDescription {
    private String name;
    private int slot = 0;
    private boolean invisible = false;
    private Material material = Material.BEDROCK;
    private String displayName = null;
    private List<String> lore = new LinkedList<>();
    private List<ItemInfo> levels = new LinkedList<>();

    public String getName() {
        return name;
    }

    public static JsonDeserializer<ItemDescription> getDeserializer(Data data) {
        return (jsonElement, type, jsonDeserializationContext) -> {
            ItemDescription result = new ItemDescription();
            JsonObject object = jsonElement.getAsJsonObject();
            result.setName(object.get("name").getAsString());
            result.setSlot(object.get("slot").getAsInt());
            if (object.has("lore")) {
                result.setLore(jsonDeserializationContext.deserialize(object.get("lore"),
                        new TypeToken<List<String>>() {
                        }.getType()));
            }
            if (object.has("material")) {
                result.setMaterial(jsonDeserializationContext.deserialize(object.get("material"), Material.class));
            }
            if (object.has("displayName")) {
                result.setDisplayName(object.get("displayName").getAsString());
            }
            if (object.has("invisible")) {
                result.setInvisible(object.get("invisible").getAsBoolean());
            }
            if (!data.getItemNameToInfo().containsKey(result.getName())) {
                throw new JsonParseException("Name: " + result.getName() + " not found in data!");
            }
            Class<?> itemInfoClass = data.getItemNameToInfo().get(result.getName());
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(itemInfoClass, new InstanceCreator<ItemInfo>() {
                @Override
                public ItemInfo createInstance(Type type) {
                    try {
                        return (ItemInfo) itemInfoClass.getConstructor(ItemDescription.class).newInstance(result);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            });
            Gson gson = builder.create();
            for (JsonElement entry : object.get("levels").getAsJsonArray()) {
                ItemInfo itemInfo = (ItemInfo) gson.fromJson(entry, itemInfoClass);
                itemInfo.prepare(result.getLevels().size() + 1);
                result.getLevels().add(itemInfo);
            }
            return result;
        };
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<ItemInfo> getLevels() {
        return levels;
    }

    public void setLevels(List<ItemInfo> levels) {
        this.levels = levels;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}
