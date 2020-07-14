package by.dero.gvh.nmcapi;

import by.dero.gvh.nmcapi.dragon.EmptyArmorStand;
import by.dero.gvh.nmcapi.dragon.RotatingDragon;
import by.dero.gvh.nmcapi.throwing.GravityFireball;
import by.dero.gvh.nmcapi.throwing.ThrowingItem;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.EntityType;

public enum CustomEntities {

    SMART_ARMOR_STAND("ThrowingItem", 30, EntityType.ARMOR_STAND, EntityArmorStand.class, ThrowingItem.class),
    EMPTY_ARMOR_STAND("EmptyArmorStand", 30, EntityType.ARMOR_STAND, EntityArmorStand.class, EmptyArmorStand.class),
    GRAVITY_FIRE_BALL("GravityFireBall", 30, EntityType.ARMOR_STAND, EntityArmorStand.class, GravityFireball.class),
    CORRECT_FIREWORK("CorrectFirework", 22, EntityType.FIREWORK, EntityFireworks.class, InstantFirework.class),
    PASSIVE_CHICKEN("PassiveChicken", 93, EntityType.CHICKEN, EntityChicken.class, ChickenAvatar.class),
    SMART_FALLING_BLOCK("SmartFallingBlock", 21, EntityType.FALLING_BLOCK, EntityFallingBlock.class, SmartFallingBlock.class),
    SMART_DRAGON("SmartDragon", 63, EntityType.ENDER_DRAGON, EntityEnderDragon.class, RotatingDragon.class),
//    CUSTOM_LEASH("CustomLeash", -1, EntityType.LEASH_HITCH, EntityLeash.class, CustomLeash.class),
//    INFINITE_FISH_HOOK("InfiniteFishHook", -1, EntityType.FISHING_HOOK, EntityFishingHook.class, InfiniteFishHook.class),
    CASTRATED_ENDER_CRYSTAL("CastratedEnderCrystal", 200, EntityType.ENDER_CRYSTAL, EntityEnderCrystal.class, CastratedEnderCrystal.class),
//    CUSTOM_LIGHTNING("CustomLightning", -1, EntityType.LIGHTNING, CustomLightning.class, EntityLightning.class),
    DRAGON_EGG("DragonEgg", 21, EntityType.FALLING_BLOCK, EntityFallingBlock.class, DragonEggEntity.class),
    D_FIREBALL("DFireball", 26, EntityType.DRAGON_FIREBALL, EntityDragonFireball.class, DFireball.class);

    private final String name;
    private final int id;
    private final EntityType entityType;
    private final Class<? extends Entity> nmsClass;
    private final Class<? extends Entity> customClass;
    private final MinecraftKey key;
    private final MinecraftKey oldKey;

    private CustomEntities(String name, int id, EntityType entityType, Class<? extends Entity> nmsClass, Class<? extends Entity> customClass) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
        this.key = new MinecraftKey(name);
        this.oldKey = EntityTypes.b.b(nmsClass);
    }

    public static void registerEntities() { for (CustomEntities ce : CustomEntities.values()) ce.register(); }
    public static void unregisterEntities() { for (CustomEntities ce : CustomEntities.values()) ce.unregister(); }

    private void register() {
        EntityTypes.d.add(key);
        EntityTypes.b.a(id, key, customClass);
    }

    private void unregister() {
        EntityTypes.d.remove(key);
//        if ()
        EntityTypes.b.a(id, oldKey, nmsClass);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Class<?> getCustomClass() {
        return customClass;
    }
}