package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.MagnetizeOrbInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class MagnetizeOrb extends Item implements ProjectileHitInterface, PlayerInteractInterface {
    public MagnetizeOrb(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        PlayerInventory inv = event.getPlayer().getInventory();
        inv.getItemInMainHand().setAmount(2);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity proj = event.getEntity();
        Location loc = proj.getLocation();
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

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}