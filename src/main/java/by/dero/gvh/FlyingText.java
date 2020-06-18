package by.dero.gvh;

import by.dero.gvh.utils.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static by.dero.gvh.utils.MessagingUtils.getNormal;

public class FlyingText {
    private final ArmorStand armorStand;

    public FlyingText(Position pos, String text, Player owner) {
        final World world = owner.getWorld();
        armorStand = (ArmorStand) world.spawnEntity(new Location(world, pos.getX(), pos.getY(), pos.getZ()), EntityType.ARMOR_STAND);

        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        setText(text);
    }

    public void setText(String text) {
        armorStand.setCustomName(getNormal(text));
    }

    public void unload() {
        armorStand.remove();
    }
}
