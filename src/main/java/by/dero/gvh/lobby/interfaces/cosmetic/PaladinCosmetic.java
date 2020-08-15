package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class PaladinCosmetic extends CosmeticInterface {
    public PaladinCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(3, 2, "fairySword"));
        registerCosmetic(new CosmeticButton(4, 2, "lostSword"));
        update();
    }
}
