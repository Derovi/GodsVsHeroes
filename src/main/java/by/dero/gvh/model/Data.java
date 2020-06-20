package by.dero.gvh.model;

import by.dero.gvh.minigame.Reward;
import by.dero.gvh.minigame.RewardManager;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import by.dero.gvh.model.items.FlyBow;
import by.dero.gvh.model.itemsinfo.FlyBowInfo;
import by.dero.gvh.model.items.*;
import by.dero.gvh.model.itemsinfo.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Data {
    public Data(StorageInterface storageInterface) {
        this.storageInterface = storageInterface;
        registerItems();
        registerClasses();
    }

    private void registerItems() {
        // IMPORTANT register all items
        registerItem("airleap", AirLeapInfo.class, AirLeap.class);
        registerItem("chainlightning", ChainLightningInfo.class, ChainLightning.class);
        registerItem("explosivebow", ExplosiveBowInfo.class, ExplosiveBow.class);
        registerItem("flybow", FlyBowInfo.class, FlyBow.class);
        registerItem("magicrod", MagicRodInfo.class, MagicRod.class);
        registerItem("magnetizeorb", MagnetizeOrbInfo.class, MagnetizeOrb.class);
        registerItem("healpotion", HealPotionInfo.class, HealPotion.class);
        registerItem("damagepotion", DamagePotionInfo.class, DamagePotion.class);
        registerItem("invisibilitypotion", InvisibilityPotionInfo.class, InvisibilityPotion.class);
        registerItem("stunrocks", StunRocksInfo.class, StunRocks.class);
        registerItem("eaglevision", EagleVisionInfo.class, EagleVision.class);
        registerItem("lightningstorm", LightningStormInfo.class, LightningStorm.class);
        registerItem("exchange", ExchangeInfo.class, Exchange.class);
        registerItem("arrowrain", ArrowRainInfo.class, ArrowRain.class);
        registerItem("escape", EscapeInfo.class, Escape.class);
        registerItem("poisonpotion", PoisonPotionInfo.class, PoisonPotion.class);
        registerItem("improvedbow", ImprovedBowInfo.class, ImprovedBow.class);
        registerItem("defaultsword", ItemInfo.class, Item.class);
        registerItem("defaultaxe", ItemInfo.class, Item.class);
        registerItem("defaulthelmet", ItemInfo.class, Item.class);
        registerItem("defaultchestplate", ItemInfo.class, Item.class);
        registerItem("defaultleggings", ItemInfo.class, Item.class);
        registerItem("defaultboots", ItemInfo.class, Item.class);
        registerItem("defaultbow", ItemInfo.class, Item.class);
        registerItem("defaultarrow", ItemInfo.class, Item.class);
        registerItem("speedbuf", ItemInfo.class, SpeedBuf.class);
        registerItem("jumpingbuf", ItemInfo.class, JumpingBuf.class);
        registerItem("resistancebuf", ItemInfo.class, ResistanceBuf.class);
        registerItem("slowbuf", ItemInfo.class, SlowBuf.class);
        registerItem("stunall", StunAllInfo.class, StunAll.class);
        registerItem("doublejump", ItemInfo.class, DoubleJump.class);
        registerItem("healall", HealAllInfo.class, HealAll.class);
        registerItem("grenade", GrenadeInfo.class, Grenade.class);
    }

    private void registerClasses() {
        registerClass("alchemist");
        registerClass("archer");
        registerClass("default");
        registerClass("eyre");
        registerClass("heimdall");
        registerClass("loki");
        registerClass("mercenary");
        registerClass("odin");
        registerClass("paladin");
        registerClass("scout");
        registerClass("ull");
    }

    public void load() {
        //load items
        try {
            for (String itemName : itemNameToClass.keySet()) {
                if (!storageInterface.exists("items", itemName)) {
                    storageInterface.save("items", itemName, ResourceUtils.readResourceFile("/items/" + itemName + ".json"));
                }
                String itemJson = storageInterface.load("items", itemName);
                Gson gson = new GsonBuilder().registerTypeAdapter(ItemDescription.class,
                        ItemDescription.getDeserializer(this)).setPrettyPrinting().create();
                items.put(itemName, gson.fromJson(itemJson, ItemDescription.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //load unit classes
        try {
            Set<String> classNames = new HashSet<>(classNameToDescription.keySet());
            for (String className : classNames) {
                if (!storageInterface.exists("classes", className)) {
                    storageInterface.save("classes", className, ResourceUtils.readResourceFile("/classes/" + className + ".json"));
                }
                String classJson = storageInterface.load("classes", className);
                Gson gson = new Gson();
                classNameToDescription.put(className, gson.fromJson(classJson, UnitClassDescription.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Data loaded!");
    }

    public void loadRewards(RewardManager manager) {
        try {
            String rewardsJson = DataUtils.loadOrDefault(storageInterface, "game", "rewards",
                    ResourceUtils.readResourceFile("/game/rewards.json"));
            manager.setRewards(new Gson().fromJson(rewardsJson, new TypeToken<HashMap<String, Reward>>() {}.getType()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerItem(String name, Class infoClass, Class<?> itemClass) {
        itemNameToInfo.put(name, infoClass);
        itemNameToClass.put(name, itemClass);
        String tag = Item.getTag(name);
        itemNameToTag.put(name, tag);
        tagToItemName.put(tag, name);
    }

    public void registerClass(String name) {
        classNameToDescription.put(name, null);
    }

    private final StorageInterface storageInterface;

    private final HashMap<String, UnitClassDescription> classNameToDescription = new HashMap<>();

    private HashMap<String, ItemDescription> items = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToInfo = new HashMap<>();
    private final HashMap<String, Class<?>> itemNameToClass = new HashMap<>();
    private final HashMap<String, String> itemNameToTag = new HashMap<>();
    private final HashMap<String, String> tagToItemName = new HashMap<>();

    public HashMap<String, UnitClassDescription> getClassNameToDescription() {
        return classNameToDescription;
    }

    public HashMap<String, String> getItemNameToTag() {
        return itemNameToTag;
    }

    public HashMap<String, String> getTagToItemName() {
        return tagToItemName;
    }

    public HashMap<String, Class<?>> getItemNameToInfo() {
        return itemNameToInfo;
    }

    public HashMap<String, Class<?>> getItemNameToClass() {
        return itemNameToClass;
    }

    public StorageInterface getStorageInterface() {
        return storageInterface;
    }

    ItemDescription getItemDescription(String name) {
        return items.get(name);
    }

    public HashMap<String, ItemDescription> getItems() {
        return items;
    }

    public void setItems(HashMap<String, ItemDescription> items) {
        this.items = items;
    }
}
