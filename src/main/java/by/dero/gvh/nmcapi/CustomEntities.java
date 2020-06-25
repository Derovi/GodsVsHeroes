package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import org.bukkit.entity.EntityType;

public enum CustomEntities {

    SMART_ARMOR_STAND("SmartArmorStand", 54, EntityType.ARMOR_STAND, EntityArmorStand.class, SmartArmorStand.class);

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