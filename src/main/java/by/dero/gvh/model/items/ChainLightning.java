package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChainLightningInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

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

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_IRONGOLEM_DEATH, 1.07f, 1);
        Drawings.drawCircleInFront(player.getEyeLocation(), 2, 3, 20, Particle.END_ROD);
        final LivingEntity entity = GameUtils.getTargetEntity(player, 100);
        if (!GameUtils.isEnemy(entity, getTeam())) {
            Drawings.drawLine(player.getEyeLocation(),
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
                Drawings.drawLine(cur.getEyeLocation(), next.getEyeLocation(), Particle.END_ROD);
                Objects.requireNonNull(next.getEyeLocation().getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, next.getEyeLocation(), 1);
                hit.add(next.getUniqueId());

                next.getWorld().playSound(next.getLocation(), Sound.ENTITY_IRONGOLEM_DEATH, 1.07f, 1);
                GameUtils.damage(damage, next, owner);
                cur = next;
                next = null;
                for (LivingEntity obj : GameUtils.getNearby(cur.getLocation(), radius)) {
                    if (GameUtils.isEnemy(obj, getTeam()) && !hit.contains(obj.getUniqueId())) {
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
