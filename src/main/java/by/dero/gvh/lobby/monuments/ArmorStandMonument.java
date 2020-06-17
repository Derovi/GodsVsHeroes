package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.utils.Position;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ArmorStandMonument extends Monument {
    private ArmorStand armorStand;

    public ArmorStandMonument(Position position, String className, Player owner) {
        super(position, className, owner);
    }

    @Override
    public void load() {
        armorStand = (ArmorStand) Lobby.getInstance().getWorld().spawnEntity(
                getPosition().toLocation(Lobby.getInstance().getWorld()), EntityType.ARMOR_STAND);
    }

    @Override
    public void unload() {
        armorStand.remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
