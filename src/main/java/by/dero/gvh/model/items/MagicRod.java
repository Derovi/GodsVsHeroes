package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.MagicRodInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class MagicRod extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final double damage;
    private final int duration;
    public MagicRod(final String name, final int level, final Player owner) {
        super(name, level, owner);
        MagicRodInfo info = (MagicRodInfo) getInfo();
        damage = info.getDamage();
        duration = info.getDuration();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.07f, 1);
        BukkitRunnable runnable = new BukkitRunnable() {
            double ticks = 0;
            int time = 0;
            final Location start = owner.getLocation().clone().add(owner.getLocation().getDirection().multiply(2));
            final HashSet<UUID> stroke = new HashSet<>();
            @Override
            public void run() {
                start.add(start.getDirection().multiply(1));
                start.getWorld().spawnParticle(Particle.LAVA, start, 1);
                for (final LivingEntity obj : GameUtils.getNearby(start, 2)) {
                    if (GameUtils.isEnemy(obj, getTeam()) && !stroke.contains(obj.getUniqueId())) {
                        obj.setFireTicks(20);
                        GameUtils.damage(damage, obj, owner);
                        stroke.add(obj.getUniqueId());
                    }
                }
                Drawings.drawCircleInFront(start, MathUtils.sin(ticks) * 3,0, Particle.SPELL_WITCH);
                ticks += Math.PI / 10;
                time++;
                if (time >= duration) {
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
        Game.getInstance().getRunnables().add(runnable);
    }
}
