package by.dero.gvh.books;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.Lang;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class ItemCharacteristicsBook extends BookGUI {
    private final String className;
    private final String itemName;

    @Getter
    @Setter
    private Runnable backAction = null;
    private int level;

    public ItemCharacteristicsBook(BookManager manager, Player player, String className, String itemName, int level) {
        super(manager, player);
        this.className = className;
        this.itemName = itemName;
        this.level = level;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add(BookUtil.TextBuilder.of("§6§l" + Lang.get("items." + itemName).replace(" ", " §6§l")).build())
                .newLine();

        ItemInfo info = Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(level);
        if (info.getLore() != null) {
            for (String line : info.getLore()) {
                if (line == null) {
                    continue;
                }
                builder.newLine().add(info.parseString(line).replace("§f", "§9"));
            }
        }

        builder.newLine().newLine().add("§dУровень:").newLine();
        for (int idx = 0; idx < info.getDescription().getLevels().size(); ++idx) {
            if (idx == level) {
                builder.add("§8»§l" + (level + 1) +  "§8« ");
            } else {
                int finalIdx = idx;
                builder.add(BookUtil.TextBuilder.of("§a[§l" + (idx + 1) + "§a]").onClick(new BookButton(this, () -> {
                    level = finalIdx;
                    build();
                    open();
                })).build()).add(" ");
            }
        }

        builder.newLine().newLine().add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
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
}
