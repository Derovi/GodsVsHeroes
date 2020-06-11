package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.MagicRodInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public class MagicRod extends Item implements PlayerInteractInterface {
    public MagicRod(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        new BukkitRunnable() {
            double ticks = 0;
            final Vector st = new Vector(
                    Math.random(),
                    Math.random(),
                    Math.random()
            ).normalize();
            final Location start = p.getLocation().clone().add(p.getLocation().getDirection().multiply(2));
            final UUID sender = p.getUniqueId();
            final double damage = ((MagicRodInfo)getInfo()).getDamage();
            @Override
            public void run() {
                start.add(start.getDirection().multiply(1));
                Vector kek = st.clone().crossProduct(start.getDirection()).multiply(Math.sin(ticks) * 3);
                Objects.requireNonNull(start.getWorld()).spawnParticle(Particle.LAVA, start, 1);
                for (Entity obj : start.getWorld().getNearbyEntities(start, 1, 1, 1)) {
                    if (!(obj instanceof Player) || obj.getUniqueId() != sender) {
                        try {
                            obj.setFireTicks(200);
                            ((Damageable)obj).damage(damage);
                        } catch (Exception ignored){}
                    }
                }
                final int steps = 16;
                for (int i = 0; i < steps; i ++) {
                    kek.rotateAroundAxis(start.getDirection(), Math.PI * 2 / steps);
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
