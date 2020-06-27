package by.dero.gvh.nmcapi.animation;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class BlockOnArm extends EntityArmorStand {
    private final ArmorStand bukkitArmorStand;

    public BlockOnArm(Location loc, Material material) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        bukkitArmorStand = (ArmorStand) this;
        bukkitArmorStand.setItemInHand(new ItemStack(material));
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.noclip = true;
    }

    public void setItem(ItemStack item) {
        bukkitArmorStand.setItemInHand(item);
    }

    public ArmorStand getBukkitArmorStand() {
        return bukkitArmorStand;
    }
}
