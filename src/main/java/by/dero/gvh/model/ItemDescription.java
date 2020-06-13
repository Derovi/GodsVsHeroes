package by.dero.gvh.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ItemDescription {
    private String name;
    private int slot = 0;
    private boolean invisible = false;
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
            result.setInvisible(object.get("invisible").getAsBoolean());
            if (!data.getItemNameToInfo().containsKey(result.getName())) {
                throw new JsonParseException("Name: " + result.getName() + " not found in data!");
            }
            Class<?> itemInfoClass = data.getItemNameToInfo().get(result.getName());
            for (JsonElement entry : object.get("levels").getAsJsonArray()) {
                result.getLevels().add(jsonDeserializationContext.deserialize(entry, itemInfoClass));
            }
            return result;
        };
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
}
