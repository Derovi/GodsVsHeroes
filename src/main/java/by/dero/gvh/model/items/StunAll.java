package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.model.Drawings.spawnMovingCircle;
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
    public void drawSign(Location loc) {
        for (double hei = 0; hei < radius; hei += 0.3) {
            spawnMovingCircle(loc.clone(), latency, radius, 2,0, Particle.FLAME, getOwner());
            loc = loc.add(0, 0.3, 0);
        }
        spawnMovingCircle(loc.clone(), latency, radius, 4,0, Particle.FLAME, getOwner());
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawSign(p.getLocation().clone());
        for (final LivingEntity ot : getNearby(p.getLocation(), radius)) {
            if (isEnemy(ot, getTeam())) {
                Stun.stunEntity(ot, latency);
            }
        }
    }
}
