package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.HealAllInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealAll extends Item implements PlayerInteractInterface {
    private final double radius;
    private final int heal;
    private final Material material;
    
    public HealAll(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final HealAllInfo info = (HealAllInfo) getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
        material = info.getMaterial();
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
        
        for (final LivingEntity ent : GameUtils.getNearby(owner.getLocation(), radius)) {
            if (GameUtils.isAlly(ent, getTeam())) {
                final double hp = Math.min(ent.getHealth() + heal,
                        ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                ent.getWorld().playEffect(ent.getLocation(), Effect.BREWING_STAND_BREW, null);

                Drawings.drawCircle(ent.getLocation(), 1, Particle.HEART);
                ent.setHealth(hp);
            }
        }
    }
}
