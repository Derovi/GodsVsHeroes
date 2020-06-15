package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class StunAll extends Item implements UltimateInterface {
    private final double radius;
    private final int latency;

    public StunAll(String name, int level, Player owner) {
        super(name, level, owner);
        StunAllInfo info = (StunAllInfo) getInfo();
        radius = info.getRadius();
        latency = info.getLatency();
    }

    @Override
    public void drawSign(Location loc) {
        int lines = 8;
        Particle part = Particle.FLAME;
        Drawings.drawCircle(loc.clone().add(0,radius,0), radius, part);
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI * 2 / lines) {
            Location at = Drawings.getInCircle(loc, radius, angle);
            Drawings.drawLine(at, at.add(0, radius,0), part);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        drawSign(p.getLocation());
        for (Entity ot : p.getNearbyEntities(radius, radius, radius)) {
            if (ot != p &&
                    ot instanceof LivingEntity &&
                    p.getLocation().distance(ot.getLocation()) < radius) {
                Stun.stunPlayer((LivingEntity)ot, latency);
            }
        }
    }
}
