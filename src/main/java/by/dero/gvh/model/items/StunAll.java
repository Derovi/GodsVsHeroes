package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class StunAll extends Item implements PlayerInteractInterface {
    private final double radius;
    private final int duration;
    private final Material material;

    public StunAll(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final StunAllInfo info = (StunAllInfo) getInfo();
        radius = info.getRadius();
        duration = info.getDuration();
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
        Location loc = owner.getLocation();
        double yaw = Math.toRadians(loc.getYaw());
        loc.add(-Math.sin(yaw) * 1.2, 0, Math.cos(yaw) * 1.2);
        Drawings.drawFist(loc, radius, Particle.FLAME);
        owner.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_GROWL, 1.07f, 1);
        for (final Entity ot : owner.getWorld().getNearbyEntities(loc, radius, 50, radius)) {
            Location otloc = ot.getLocation();

            double dst = new Vector(loc.x - otloc.x, 0, loc.z - otloc.z).length();
            if (otloc.y > loc.y + radius || otloc.y + 2 < loc.y) {
                continue;
            }
            if (dst < radius && GameUtils.isEnemy(ot, getTeam())) {
                Stun.stunEntity((LivingEntity) ot, duration);
            }
        }
    }
}
