package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class WarriorCosmetic extends CosmeticInterface {
    public WarriorCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(2, 2, "bloodySword"));
        registerCosmetic(new CosmeticButton(3, 2, "victorySword"));
        update();
    }
}
