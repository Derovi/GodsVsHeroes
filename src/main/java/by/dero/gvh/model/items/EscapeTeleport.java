package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.EscapeTeleportInfo;
import by.dero.gvh.nmcapi.PlayerUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EscapeTeleport extends Item implements DoubleSpaceInterface {
    private final double radius;
    private final double minradius;
    public EscapeTeleport(String name, int level, Player owner) {
        super(name, level, owner);
        final EscapeTeleportInfo info = (EscapeTeleportInfo) getInfo();
        radius = info.getRadius();
        minradius = info.getMinRadius();
    }

    @Override
    public void onDoubleSpace() {
        final Location loc = MathUtils.getGoodInCylinder(owner.getLocation().clone(), minradius, radius);
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        Drawings.drawCphere(owner.getLocation().clone(), 1.5, Particle.SMOKE_LARGE);
        PlayerUtils.jumpDown(owner, 10);
        owner.setInvulnerable(true);
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Drawings.drawLineColor(loc.clone(), owner.getLocation().clone(), 255, 0, 0);
                owner.teleport(loc.clone().add(0,-2,0));
                PlayerUtils.jumpUp(owner, 10);
                Drawings.drawCphere(loc.clone(), 1.5, Particle.SMOKE_LARGE);
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                owner.setInvulnerable(false);
            }
        }.runTaskLater(Plugin.getInstance(), 20);
        Minigame.getInstance().getGame().getRunnables().add(runnable);
    }
}
