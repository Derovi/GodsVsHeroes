package by.dero.gvh.bookapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public abstract class BookGUI {
    private final HashMap<UUID, BookButton> buttons = new HashMap<>();
    private final BookManager manager;
    private final Player player;
    private ItemStack book;

    public BookGUI(BookManager manager, Player player) {
        this.manager = manager;
        this.player = player;
    }

    abstract void build();

    public void open() {
        manager.registerBook(player, this);
        BookUtil.openPlayer(player, book);
    }

    public HashMap<UUID, BookButton> getButtons() {
        return buttons;
    }

    public BookManager getManager() {
        return manager;
    }

    public Player getPlayer() {
        return player;
    }

    public void setBook(ItemStack book) {
        this.book = book;
    }

    public ItemStack getBook() {
        return book;
    }
}
