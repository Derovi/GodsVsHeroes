package by.dero.gvh;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class FlyingText {
    private final ArmorStand armorStand;

    public FlyingText(final Location pos, final String text) {
        armorStand = (ArmorStand) pos.getWorld().spawnEntity(pos, EntityType.ARMOR_STAND);

        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        setText(text);
    }

    public void setText(String text) {
        armorStand.setCustomName(text);
    }

    public void unload() {
        armorStand.remove();
    }
}
