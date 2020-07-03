package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.HealAllInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealAll extends Item implements UltimateInterface {
    private final double radius;
    private final int heal;
    public HealAll(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final HealAllInfo info = (HealAllInfo) getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
    }

    @Override
    public void drawSign(final Location loc) {
        for (final LivingEntity ent : loc.getWorld().getLivingEntities()) {
            if (ent.getLocation().distance(loc) <= radius) {
                Drawings.drawCircle(ent.getLocation(), 1, Particle.HEART);
            }
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawSign(p.getLocation());
        for (final LivingEntity ent : GameUtils.getNearby(p.getLocation(), radius)) {
            if (GameUtils.isAlly(ent, getTeam())) {
                final double hp = Math.min(ent.getHealth() + heal,
                        ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                ent.setHealth(hp);
            }
        }
    }
}
