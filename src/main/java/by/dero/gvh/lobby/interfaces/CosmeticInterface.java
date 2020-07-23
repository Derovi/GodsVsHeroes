package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.model.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticInterface extends Interface {
    private final String className;
    private Runnable onBackButton = null;

    public CosmeticInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, Lang.get("cosmetic.titleHero").replace("%hero",
                Lang.get("classes." + className)));
        this.className = className;
        ItemStack backItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        addButton(0, 6, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
        addButton(1, 6, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
        addButton(0, 5, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
    }

    public Runnable getOnBackButton() {
        return onBackButton;
    }

    public CosmeticInterface setOnBackButton(Runnable onBackButton) {
        this.onBackButton = onBackButton;
        return this;
    }
}
