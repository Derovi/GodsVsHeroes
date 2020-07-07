package by.dero.gvh.model.items;

import by.dero.gvh.GameMob;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.FireSpearInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

public class FireSpear extends Item implements PlayerInteractInterface {
    private final int parts = 6;
    private final double speed = 30;
    private final int time = 140;
    private final double damage;
    public FireSpear(final String name, final int level, final Player owner) {
        super(name, level, owner);
        damage = ((FireSpearInfo) getInfo()).getDamage();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();

        final Location loc = event.getPlayer().getEyeLocation().clone();
        loc.add(loc.getDirection().multiply(2));
        final Vector dlt = loc.getDirection().multiply(0.05 * speed);
        final HashSet<LivingEntity> left = new HashSet<>();
        for (final GamePlayer gp : Game.getInstance().getPlayers().values()) {
            if (GameUtils.isEnemy(gp.getPlayer(), getTeam())) {
                left.add(gp.getPlayer());
            }
        }
        for (GameMob gm : Game.getInstance().getMobs().values()) {
            if (GameUtils.isEnemy(gm.getEntity(), getTeam())) {
                left.add(gm.getEntity());
            }
        }
        owner.getWorld().playSound(loc, Sound.BLOCK_CHORUS_FLOWER_GROW, 1.07f, 1);
        final BukkitRunnable runnable = new BukkitRunnable() {
            int ticks = 0;
            final ArrayList<LivingEntity> rem = new ArrayList<>();
            @Override
            public void run() {
                rem.clear();
                for (final LivingEntity p : left) {
                    if (p.getLocation().distance(loc) < 3 || p.getEyeLocation().distance(loc) < 3) {
                        rem.add(p);
                        GameUtils.damage(damage, p, owner);
                        p.setFireTicks(60);
                    }
                }
                left.removeAll(rem);
                rem.clear();

                for (int i = 0; i < 3; i++) {
                    Drawings.drawCircleInFront(loc, i * 0.33, -i, parts, Particle.FLAME);
                }
                Drawings.drawLine(loc, loc.clone().subtract(dlt.clone().multiply(Math.min(10, ticks))), Particle.END_ROD);
                loc.add(dlt.clone().multiply(2));
                ticks += 2;
                if (ticks >= time) {
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);

    }
}
