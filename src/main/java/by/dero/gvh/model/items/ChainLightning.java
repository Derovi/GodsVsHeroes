package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChainLightningInfo;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static by.dero.gvh.model.Drawings.*;
import static by.dero.gvh.utils.DataUtils.*;

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
            return;
        }
        cooldown.reload();

        drawCircleInFront(player.getEyeLocation(), 2, 3, 20, Particle.END_ROD);
        final LivingEntity entity = getTargetEntity(player, 100);
        if (entity == null || !isEnemy(entity, getTeam())) {
            drawLine(player.getEyeLocation(),
                    player.getEyeLocation().clone().add(player.getLocation().getDirection().multiply(100)),
                    Particle.END_ROD);
            return;
        }
        new BukkitRunnable() {
            final HashSet<UUID> hit = new HashSet<>();
            LivingEntity cur = player;
            LivingEntity next = entity;
            @Override
            public void run() {
                drawLine(cur.getEyeLocation(), next.getEyeLocation(), Particle.END_ROD);
                Objects.requireNonNull(next.getEyeLocation().getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, next.getEyeLocation(), 1);
                hit.add(next.getUniqueId());
                damage(damage, next, getOwner());
                cur = next;
                next = null;
                for (LivingEntity obj : getNearby(cur.getLocation(), radius)) {
                    if (isEnemy(obj, getTeam()) && !hit.contains(obj.getUniqueId())) {
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
