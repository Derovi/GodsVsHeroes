package by.dero.gvh.model.kits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public abstract class LootBoxItem {
    @Getter
    private final String playerName;

    public LootBoxItem(String playerName, int chance) {
        this.playerName = playerName;
        this.chance = chance;
    }

    @Getter @Setter
    private String name;
    @Getter @Setter
    private int chance;

    public abstract void give();
    public abstract ItemStack getItemStack();
}
