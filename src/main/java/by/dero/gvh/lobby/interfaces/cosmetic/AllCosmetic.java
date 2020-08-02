package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.CosmeticInterface;
import by.dero.gvh.lobby.interfaces.InterfaceManager;
import org.bukkit.entity.Player;

public class AllCosmetic extends CosmeticInterface {
    public AllCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(3, 3, "headDrop"));
        registerCosmetic(new CosmeticButton(5, 3, "grave"));
        update();
    }
}
