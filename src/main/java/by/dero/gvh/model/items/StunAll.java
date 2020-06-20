package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;

public class StunAll extends Item implements UltimateInterface {
    private final double radius;
    private final int latency;

    public StunAll(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final StunAllInfo info = (StunAllInfo) getInfo();
        radius = info.getRadius();
        latency = info.getLatency();
    }

    @Override
    public void drawSign(final Location loc) {
        final int lines = 8;
        final Particle part = Particle.FLAME;
        Drawings.drawCircle(loc.clone().add(0,radius,0), radius, part);
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI * 2 / lines) {
            final Location at = Drawings.getInCircle(loc, radius, angle);
            Drawings.drawLine(at, at.add(0, radius,0), part);
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        drawSign(p.getLocation());
        for (final LivingEntity ot : getNearby(p.getLocation(), radius)) {
            if (isEnemy(ot, getTeam())) {
                Stun.stunEntity(ot, latency);
            }
        }
    }
}
