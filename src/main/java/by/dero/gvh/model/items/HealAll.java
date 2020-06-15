package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.HealAllInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealAll extends Item implements UltimateInterface {
    private final double radius;
    private final int heal;
    public HealAll(String name, int level, Player owner) {
        super(name, level, owner);
        HealAllInfo info = (HealAllInfo) getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
    }

    @Override
    public void drawSign(Location loc) {
        for (final LivingEntity ent : loc.getWorld().getLivingEntities()) {
            if (ent.getLocation().distance(loc) <= radius) {
                Drawings.drawCircle(ent.getLocation(), 1, Particle.HEART);
            }
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        drawSign(p.getLocation());
        for (final Entity ent : p.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof LivingEntity && ent.getLocation().distance(p.getLocation()) <= radius) {
                LivingEntity cur = (LivingEntity) ent;
                final double hp = Math.min(cur.getHealth() + heal,
                        cur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                cur.setHealth(hp);
            }
        }
    }
}
