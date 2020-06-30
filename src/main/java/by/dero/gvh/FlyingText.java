package by.dero.gvh;

import by.dero.gvh.utils.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class FlyingText {
    private final CraftArmorStand armorStand;

    public FlyingText(final Location pos, final String text) {
        armorStand = (CraftArmorStand) pos.getWorld().spawnEntity(pos, EntityType.ARMOR_STAND);

        DataUtils.setInvisibleStand(armorStand);
        armorStand.getHandle().setCustomNameVisible(true);
        armorStand.getHandle().setMarker(true);
        setText(text);
    }

    public void setText(String text) {
        armorStand.setCustomName(text);
    }

    public void unload() {
        armorStand.remove();
    }
}
