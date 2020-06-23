package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.MagicRodInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static by.dero.gvh.model.Drawings.rotateAroundAxis;
import static by.dero.gvh.utils.DataUtils.*;

public class MagicRod extends Item implements PlayerInteractInterface {
    private final double damage;
    public MagicRod(final String name, final int level, final Player owner) {
        super(name, level, owner);
        damage = ((MagicRodInfo) getInfo()).getDamage();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        new BukkitRunnable() {
            double ticks = 0;
            final Vector st = new Vector(
                    Math.random(),
                    Math.random(),
                    Math.random()
            ).normalize();
            final Location start = player.getLocation().clone().add(player.getLocation().getDirection().multiply(2));
            final HashSet<UUID> stroke = new HashSet<>();
            @Override
            public void run() {
                start.add(start.getDirection().multiply(1));
                final Vector kek = st.clone().crossProduct(start.getDirection()).multiply(Math.sin(ticks) * 3);
                Objects.requireNonNull(start.getWorld()).spawnParticle(Particle.LAVA, start, 1);
                for (final LivingEntity obj : getNearby(start, 2)) {
                    if (isEnemy(obj, getTeam()) && !stroke.contains(obj.getUniqueId())) {
                        damage(damage, obj, getOwner());
                        stroke.add(obj.getUniqueId());
                    }
                }
                final int steps = 16;
                for (int i = 0; i < steps; i ++) {
                    rotateAroundAxis(kek, start.getDirection(), Math.PI * 2 / steps);
                    start.getWorld().spawnParticle(Particle.SPELL_WITCH, new Location(
                            start.getWorld(),
                            start.getX() + kek.getX(),
                            start.getY() + kek.getY(),
                            start.getZ() + kek.getZ()
                    ), 1);
                }
                ticks += Math.PI / 10;
                if (ticks >= 10000) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }
}
