package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ThrowingItem extends EntityArmorStand {
    private final ArmorStand armorStand;

    public ThrowingItem(Location loc, Material material) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        armorStand = (ArmorStand) getBukkitEntity();
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setYawPitch(loc.getYaw(), loc.getPitch());
        this.setRightArmPose(new Vector3f(270,0, 0));
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.noclip = true;
        setItem(new ItemStack(material));
    }

    public void setRotation(EulerAngle eulerAngle) {
        armorStand.setRightArmPose(eulerAngle);
    }

    public void setItem(ItemStack item) {
        armorStand.setItemInHand(item);
    }

    public void setVelocity(Vector vector) {
        armorStand.setVelocity(vector);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    @Override
    public void move(EnumMoveType moveType, double x, double y, double z) {
        super.move(moveType, x, y, z);
        System.out.println("Move lol " + x + ' ' + y + ' ' + z);
    }

    public void spawn() {
        ((CraftWorld) armorStand.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
