package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.EagleVisionInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EagleVision extends Item implements PlayerInteractInterface {
    private final double particleDense = 32;

    private final double radius;
    private final Long glowTime;
    private final Particle searchParticle;

    public EagleVision(final String name, final int level, final Player owner) {
        super(name, level, owner);

        final EagleVisionInfo info = (EagleVisionInfo) getInfo();
        radius = info.getRadius();
        glowTime = info.getGlowTime();
        searchParticle = info.getSearchParticle();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final Location loc = owner.getLocation().clone();
        owner.getWorld().playSound(loc, Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.07f, 1);
        final int team = ownerGP.getTeam();
        for (final Entity obj : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, radius, 200, radius)) {
            final Location cur = obj.getLocation().clone();
            final double dst = loc.distance(new Location(cur.getWorld(), cur.getX(), loc.getY(), cur.getZ()));
            if (GameUtils.isEnemy(obj, team) && dst <= radius) {
                new PotionEffect(PotionEffectType.GLOWING, Math.toIntExact(glowTime), 1).apply((LivingEntity) obj);
                if (obj instanceof Player) {
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        p.showPlayer(Plugin.getInstance(), (Player) obj);
                    }
                }
            }
        }
        drawSign(owner.getLocation().clone());
    }

    public void drawSign(final Location loc) {
        new BukkitRunnable(){
            double r = 1, addAngle = 0;
            @Override
            public void run() {
                for (double angle = addAngle; angle < MathUtils.PI2 + addAngle; angle += Math.PI * 2 / particleDense) {
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(searchParticle, new Location(
                            loc.getWorld(),
                            loc.getX() + r * MathUtils.cos(angle),
                            loc.getY(),
                            loc.getZ() + r * MathUtils.sin(angle)
                    ), 1,0,0,0,0);
                }

                r += 0.5;
                addAngle += Math.PI / particleDense / 4;
                if (r > radius) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }
}
