package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class HorsemanCosmetic extends CosmeticInterface {
    public HorsemanCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(2, 2, "skeletonAxe"));
        registerCosmetic(new CosmeticButton(3, 2, "hellAxe"));
        update();
    }
}
