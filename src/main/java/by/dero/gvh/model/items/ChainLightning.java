package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChainLightningInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;
import static by.dero.gvh.utils.MessagingUtils.sendCooldownMessage;

public class ChainLightning extends Item implements PlayerInteractInterface {
    private final double radius;
    private final double damage;

    public ChainLightning(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final ChainLightningInfo info = (ChainLightningInfo) getInfo();
        damage = info.getDamage();
        radius = info.getRadius();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            sendCooldownMessage(player, getInfo().getDisplayName(), cooldown.getSecondsRemaining());
            return;
        }
        cooldown.reload();
        RayTraceResult ray = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                100, (p) -> isEnemy(p, team)
        );
        if (ray == null || !isEnemy(ray.getHitEntity(), team)) {
            Drawings.drawLine(player.getEyeLocation(),
                    player.getEyeLocation().clone().add(player.getLocation().getDirection().multiply(100)),
                    Particle.FIREWORKS_SPARK);
            return;
        }
        new BukkitRunnable() {
            final HashSet<UUID> hit = new HashSet<>();
            LivingEntity cur = player;
            LivingEntity next = (LivingEntity) ray.getHitEntity();
            @Override
            public void run() {
                Drawings.drawLine(cur.getEyeLocation(), next.getEyeLocation(), Particle.FIREWORKS_SPARK);
                Objects.requireNonNull(next.getEyeLocation().getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, next.getEyeLocation(), 1);
                hit.add(next.getUniqueId());
                next.damage(damage, getOwner());
                cur = next;
                next = null;
                for (LivingEntity obj : getNearby(cur.getLocation(), radius)) {
                    if (isEnemy(obj, team) && !hit.contains(obj.getUniqueId())) {
                        next = obj;
                        break;
                    }
                }
                if (next == null) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(),0, 5);
    }
}
