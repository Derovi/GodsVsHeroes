package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class Sword extends Item {
    public Sword(String name, int level, Player owner) {
        super(name, level, owner);
        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0D);
        owner.saveData();
    }
}
