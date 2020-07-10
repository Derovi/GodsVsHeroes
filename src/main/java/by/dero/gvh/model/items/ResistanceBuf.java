package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ResistanceBuf extends Item {
    public ResistanceBuf(final String name, final int level, final Player owner) {
        super(name, level, owner);
        GameUtils.getPlayer(owner.getName()).addEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int)2e9, 0));
    }
}
