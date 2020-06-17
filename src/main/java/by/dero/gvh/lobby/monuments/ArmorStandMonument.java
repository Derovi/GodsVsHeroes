package by.dero.gvh.lobby.monuments;

import by.dero.gvh.utils.Position;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class ArmorStandMonument extends Monument {
    private ArmorStand armorStand;

    public ArmorStandMonument(Position position, String className, Player owner) {
        super(position, className, owner);
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
