package by.dero.gvh.books;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.books.ItemCharacteristicsBook;
import by.dero.gvh.model.Lang;
import org.bukkit.entity.Player;

public class ItemDescriptionBook extends BookGUI {
    private final String className;
    private final String itemName;
    private Runnable backAction = null;

    public ItemDescriptionBook(BookManager manager, Player player, String className, String itemName) {
        super(manager, player);
        this.className = className;
        this.itemName = itemName;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add(BookUtil.TextBuilder.of("§6§l" + Lang.get("items." + itemName).replace(" ", " §6§l")).build())
                .newLine().newLine()
                .add(Lang.get("desc." + itemName));

       /* ItemInfo info = Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(
                Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName()).getItemLevel(className, itemName));
        if (info.getLore() != null) {
            for (String line : info.getLore()) {
                if (line == null) {
                    continue;
                }
                builder.newLine().add(info.parseString(line).replace("§f", "§9"));
            }
        }

        builder.newLine();

        if (Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName()).isClassUnlocked(className)) {
            builder.newLine().add(BookUtil.TextBuilder.of("§b[УЛУЧШИТЬ]").onClick(new BookButton(this, () -> {
                // TODO improve
            })).build());
        }
*/
        builder.newLine().newLine().add(BookUtil.TextBuilder.of("§3§l[Характеристики]").onClick(new BookButton(this, () -> {
            ItemCharacteristicsBook book = new ItemCharacteristicsBook(getManager(), getPlayer(), className, itemName,
                    Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName()).getItemLevel(className, itemName));
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build());

        builder.newLine().add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
            if (backAction != null) {
                backAction.run();
            }
        })).build());

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title(itemName)
                .pages(builder.build()
                ).build());
    }

    public Runnable getBackAction() {
        return backAction;
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }
}
