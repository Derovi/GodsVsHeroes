package by.dero.gvh.model.kits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public abstract class KitEntry {
    @Getter
    private final String playerName;

    public KitEntry(String playerName) {
        this.playerName = playerName;
    }

    @Getter
    @Setter
    private String name;

    public abstract void give();
    public abstract ItemStack getItemStack();
}
