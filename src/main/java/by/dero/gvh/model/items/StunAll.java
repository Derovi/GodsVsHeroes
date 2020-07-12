package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class StunAll extends Item implements PlayerInteractInterface {
    private final double radius;
    private final int latency;

    public StunAll(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final StunAllInfo info = (StunAllInfo) getInfo();
        radius = info.getRadius();
        latency = info.getLatency();
    }

    public void drawSign(Location loc) {
        for (double hei = 0; hei < radius; hei += 0.3) {
            Drawings.drawCircle(loc.clone(), radius, Particle.FLAME);
            loc = loc.add(0, 0.3, 0);
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();

        Location loc = owner.getLocation();
        for (double hei = 0; hei < radius; hei += 0.4) {
            Drawings.drawCircle(loc.clone(), radius, Particle.FLAME);
            loc = loc.add(0, 0.4, 0);
        }
        owner.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_GROWL, 1.07f, 1);
        for (final LivingEntity ot : owner.getWorld().getLivingEntities()) {
            Location otloc = ot.getLocation();
            if (otloc.y > loc.y + radius || otloc.y + 2 < loc.y) {
                continue;
            }
            double dst = new Vector(loc.x - otloc.x, 0, loc.z - otloc.z).length();
            if (dst < radius && GameUtils.isEnemy(ot, getTeam())) {
                Stun.stunEntity(ot, latency);
            }
        }
    }
}
