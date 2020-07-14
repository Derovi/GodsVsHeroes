package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowBuf extends Item {
    public SlowBuf(final String name, final int level, final Player owner) {
        super(name, level, owner);
        ownerGP.addEffect(new PotionEffect(PotionEffectType.SLOW, (int)2e9, 0));
    }
}
