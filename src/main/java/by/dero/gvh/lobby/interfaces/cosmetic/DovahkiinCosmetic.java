package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class DovahkiinCosmetic extends CosmeticInterface {
    public DovahkiinCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(4, 2, "dragonSword"));
        update();
    }
}
