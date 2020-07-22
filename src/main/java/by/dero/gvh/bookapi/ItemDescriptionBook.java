package by.dero.gvh.bookapi;

import by.dero.gvh.GamePlayer;
import org.bukkit.inventory.ItemStack;

public class ItemDescriptionBook {
    private GamePlayer player;
    private String itemName;
    private ItemStack book;

    public ItemDescriptionBook(GamePlayer player, String itemName) {
        this.player = player;
        this.itemName = itemName;
        BookUtil.writtenBook()
                .author("§6derovi")
                .title(itemName)
                .pages(new BookUtil.PageBuilder()
                        .add().build()
                );
    }

    public void open() {
        BookUtil.openPlayer(player.getPlayer(), book);
    }
}
