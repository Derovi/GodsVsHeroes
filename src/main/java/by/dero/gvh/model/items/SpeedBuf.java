package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBuf extends Item {
    public SpeedBuf(final String name, final int level, final Player owner) {
        super(name, level, owner);
        ownerGP.addEffect(new PotionEffect(PotionEffectType.SPEED, (int)2e9, 0));
    }
}
