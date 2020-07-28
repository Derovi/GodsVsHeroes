package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class AssassinCosmetic extends CosmeticInterface {
    public AssassinCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(3, 2, "ezioEspadron"));
        registerCosmetic(new CosmeticButton(4, 2, "altairBlade"));
        update();
    }
}
