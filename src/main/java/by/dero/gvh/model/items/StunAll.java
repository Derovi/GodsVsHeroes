package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.StunAllInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

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
            Drawings.spawnMovingCircle(loc.clone(), latency, radius, 2,0, Particle.FLAME, owner.getWorld());
            loc = loc.add(0, 0.3, 0);
        }
        Drawings.spawnMovingCircle(loc.clone(), latency, radius, 4,0, Particle.FLAME, owner.getWorld());
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawSign(p.getLocation().clone());
        for (final LivingEntity ot : GameUtils.getNearby(p.getLocation(), radius)) {
            if (GameUtils.isEnemy(ot, getTeam())) {
                Stun.stunEntity(ot, latency);
            }
        }
    }
}
