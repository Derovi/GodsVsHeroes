package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.MagnetizeOrbInfo;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class MagnetizeOrb extends Item implements ProjectileHitInterface, ProjectileLaunchInterface, InteractInterface {
    public MagnetizeOrb(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        PlayerInventory inv = event.getPlayer().getInventory();
        inv.addItem(inv.getItemInMainHand());
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        proj.setMetadata("magnet", new FixedMetadataValue(Plugin.getInstance(), ""));
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity proj = event.getEntity();
        Location loc = proj.getLocation();
        if (proj.hasMetadata("magnet")) {
            double radius = ((MagnetizeOrbInfo)getInfo()).getRadius();
            for (Entity obj : proj.getNearbyEntities(radius, radius, radius)) {
                if (loc.distance(obj.getLocation()) > radius) {
                    continue;
                }
                Vector add = loc.toVector().subtract(obj.getLocation().toVector());
                double force = Math.log(add.length()) / Math.log(3);
                obj.setVelocity(obj.getVelocity().add(add.normalize().multiply(force)));
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}