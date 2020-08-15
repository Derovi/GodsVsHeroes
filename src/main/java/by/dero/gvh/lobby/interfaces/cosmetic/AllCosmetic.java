package by.dero.gvh.lobby.interfaces.cosmetic;

import by.dero.gvh.lobby.interfaces.InterfaceManager;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AllCosmetic extends CosmeticInterface {
    public AllCosmetic(InterfaceManager manager, Player player, String className) {
        super(manager, player, className);
        registerCosmetic(new CosmeticButton(3, 3, "headDrop"));
        registerCosmetic(new CosmeticButton(5, 3, "grave"));
        registerCosmetic(new CosmeticButton(7, 3, "creeperFirework"));
        
        update();
    }
    
    @Override
    public void update() {
        super.update();
        ItemStack deathTextItem = new ItemStack(Material.SIGN);
        InterfaceUtils.changeName(deathTextItem, Lang.get("cosmetic.deathText"));
    
        deathTextItem.setLore(Collections.singletonList(Lang.get("cosmetic.deathTextLore")));
        addButton(8, getHeight()-1, deathTextItem, () -> {
            DeathTextCosmetics inter = new DeathTextCosmetics(getManager(), getPlayer());
            inter.setOnBackButton(this::open);
            close();
            inter.open();
        });
    }
}
