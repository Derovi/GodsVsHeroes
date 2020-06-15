package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JumpingBuf extends Item {
    public JumpingBuf(final String name, final int level, final Player owner) {
        super(name, level, owner);
        if (level != 0) {
            new PotionEffect(PotionEffectType.JUMP, (int)2e9, level).apply(owner);
        }
    }
}
