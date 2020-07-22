package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChainLightningInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class ChainLightning extends Item implements PlayerInteractInterface {
    private final double radius;
    private final double damage;
    private final double otherDamage;
    private final Material material;

    public ChainLightning(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final ChainLightningInfo info = (ChainLightningInfo) getInfo();
        damage = info.getDamage();
        radius = info.getRadius();
        otherDamage = info.getOtherDamage();
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_IRONGOLEM_DEATH, 1.07f, 1);
        Drawings.drawCircleInFront(owner.getEyeLocation(), 2, 3, Particle.END_ROD);
        final LivingEntity entity = GameUtils.getTargetEntity(owner, 40, 1.5, e -> GameUtils.isEnemy(e, getTeam()));
        if (!GameUtils.isEnemy(entity, getTeam())) {
            Drawings.drawLine(owner.getEyeLocation(),
                    owner.getEyeLocation().clone().add(owner.getLocation().getDirection().multiply(40)),
                    Particle.END_ROD);
            return;
        }
        new BukkitRunnable() {
            final HashSet<UUID> hit = new HashSet<>();
            LivingEntity cur = owner;
            LivingEntity next = entity;
            double curDamage = damage;
            @Override
            public void run() {
                Drawings.drawLine(cur.getEyeLocation(), next.getEyeLocation(), Particle.END_ROD);
                next.getEyeLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, next.getEyeLocation(), 1);
                hit.add(next.getUniqueId());

                next.getWorld().playSound(next.getLocation(), Sound.ENTITY_IRONGOLEM_DEATH, 1.07f, 1);
                GameUtils.damage(curDamage, next, owner);
                cur = next;
                next = null;
                for (LivingEntity obj : GameUtils.getNearby(cur.getLocation(), radius)) {
                    if (GameUtils.isEnemy(obj, getTeam()) && !hit.contains(obj.getUniqueId())) {
                        next = obj;
                        break;
                    }
                }
                if (curDamage == damage) {
                    curDamage = otherDamage;
                }
                if (next == null) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(),0, 5);
    }
}
