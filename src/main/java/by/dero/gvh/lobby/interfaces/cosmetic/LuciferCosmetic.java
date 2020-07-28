package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class LuciferCosmetic extends CosmeticInterface {
    public LuciferCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(3, 2, "demoicSword"));
        registerCosmetic(new CosmeticButton(4, 2, "devilSword"));
        update();
    }
}
