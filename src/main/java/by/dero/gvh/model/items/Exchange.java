package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExchangeInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.model.Drawings.drawCircleInFront;
import static by.dero.gvh.model.Drawings.randomCylinder;
import static by.dero.gvh.utils.DataUtils.*;

public class Exchange extends Item implements PlayerInteractInterface {
    private final double maxRange;
    public Exchange(String name, int level, Player owner) {
        super(name, level, owner);
        maxRange = ((ExchangeInfo) getInfo()).getMaxRange();
    }

    final int parts = 300;
    public void drawSign(final LivingEntity player) {
        double radius = 0.7;
        for (int ticks = 0; ticks < 5; ticks++) {
            drawCircleInFront(player, radius, 3, 5, Particle.PORTAL);
            radius += 0.3;
        }

        drawCircleInFront(player, radius, 3, 20, Particle.PORTAL);
        for (int i = 0; i < parts; i++) {
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                    randomCylinder(player.getLocation(), 1.3, -2), 0);
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final LivingEntity target = getTargetEntity(player, maxRange);

        final Location zxc = player.getLocation().clone();
        if (target != null) {
            if (!cooldown.isReady()) {
                return;
            }
            cooldown.reload();
            drawSign(player);
            drawSign(target);
            player.teleport(target);
            target.teleport(zxc);
        }
    }
}
