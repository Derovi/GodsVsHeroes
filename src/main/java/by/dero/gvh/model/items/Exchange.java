package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExchangeInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

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
            Drawings.drawCircleInFront(player.getEyeLocation(), radius, 3, Particle.PORTAL);
            radius += 0.3;
        }

        Drawings.drawCircleInFront(player.getEyeLocation(), radius, 3, Particle.PORTAL);
        for (int i = 0; i < parts; i++) {
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                    MathUtils.randomCylinder(player.getLocation(), 1.3, -2), 0);
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final LivingEntity target = GameUtils.getTargetEntity(player, maxRange, (e) -> GameUtils.isEnemy(e, getTeam()));

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
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE, 1.07f, 1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE, 1.07f, 1);
        }
    }
}
